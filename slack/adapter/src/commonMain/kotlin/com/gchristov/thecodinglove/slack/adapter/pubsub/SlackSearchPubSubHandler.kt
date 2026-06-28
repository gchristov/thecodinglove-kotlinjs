package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandReceivedEvent
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSearchPubSubHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    override val pubSubDecoder: PubSubDecoder,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackSearchRepository: SlackSearchRepository,
    private val analytics: Analytics,
) : PubSubHandler<SlackSlashCommandReceivedEvent> {
    override val strategy = SlackSlashCommandReceivedEvent.serializer()

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/slash-command-received",
        contentType = ContentType.Application.Json,
    )

    /*
     * Propagates errors so that PubSub retries on network failures. Handles search errors internally by posting
     * an error message back to the user, so those don't cause retries.
     */
    override suspend fun handle(event: SlackSlashCommandReceivedEvent): Either<Throwable, Unit> {
        analytics.sendEvent(
            clientId = event.userId,
            name = "slack_slash_command",
            params = mapOf(
                "command" to event.command,
                "text" to event.text,
                "user_id" to event.userId,
                "team_id" to event.teamId,
            )
        )
        return slackRepository.postMessageToUrl(
            url = event.responseUrl,
            message = slackMessageFactory.searchingMessage(),
        )
            .flatMap { slackSearchRepository.search(event.text) }
            .fold(
                ifLeft = { error ->
                    analytics.sendEvent(
                        clientId = event.userId,
                        name = "slack_slash_command_error",
                        params = error.message?.let { mapOf("type" to "generic", "info" to it) }
                    )
                    slackRepository.postMessageToUrl(
                        url = event.responseUrl,
                        message = slackMessageFactory.searchGenericErrorMessage()
                    )
                },
                ifRight = { searchResult ->
                    val searchSession = searchResult.searchSession
                    when {
                        searchResult.ok && searchSession != null -> {
                            analytics.sendEvent(
                                clientId = event.userId,
                                name = "slack_slash_command_success",
                                params = mapOf(
                                    "query" to searchSession.post.searchQuery,
                                    "post_title" to searchSession.post.attachmentTitle,
                                ),
                            )
                            slackRepository.postMessageToUrl(
                                url = event.responseUrl,
                                message = slackMessageFactory.searchResultMessage(
                                    searchQuery = searchSession.post.searchQuery,
                                    searchResults = searchSession.searchResults,
                                    searchSessionId = searchSession.searchSessionId,
                                    attachmentTitle = searchSession.post.attachmentTitle,
                                    attachmentUrl = searchSession.post.attachmentUrl,
                                    attachmentImageUrl = searchSession.post.attachmentImageUrl,
                                )
                            )
                        }
                        else -> when (searchResult.error) {
                            is SlackSearchRepository.SearchResultDto.Error.NoResults -> {
                                analytics.sendEvent(
                                    clientId = event.userId,
                                    name = "slack_slash_command_error",
                                    params = mapOf("type" to "no_results")
                                )
                                slackRepository.postMessageToUrl(
                                    url = event.responseUrl,
                                    message = slackMessageFactory.noSearchResultsMessage(event.text)
                                )
                            }
                            null -> {
                                analytics.sendEvent(
                                    clientId = event.userId,
                                    name = "slack_slash_command_error",
                                    params = mapOf("type" to "generic")
                                )
                                slackRepository.postMessageToUrl(
                                    url = event.responseUrl,
                                    message = slackMessageFactory.searchGenericErrorMessage()
                                )
                            }
                        }
                    }
                }
            )
    }
}
