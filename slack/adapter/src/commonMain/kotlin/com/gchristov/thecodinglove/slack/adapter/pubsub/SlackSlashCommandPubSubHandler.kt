package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.PubSubSlackSlashCommandMessage
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSlashCommandPubSubHandler(
    dispatcher: CoroutineDispatcher,
    jsonSerializer: JsonSerializer,
    log: Logger,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackSearchRepository: SlackSearchRepository,
    private val analytics: Analytics,
    pubSubDecoder: PubSubDecoder,
) : BasePubSubHandler<PubSubSlackSlashCommandMessage>(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
    pubSubDecoder = pubSubDecoder,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/slash",
        contentType = ContentType.Application.Json,
    )

    override fun deserialisationStrategy() = PubSubSlackSlashCommandMessage.serializer()

    /*
     * This method handles errors as success without PubSub retries, but tries to notify the user of the error. If
     * sending the Slack reply back fails, the entire PubSub chain will be retried automatically. This is to avoid
     * unnecessary PubSub retries which would most likely result in additional errors if the problem is on our end.
     */
    override suspend fun handlePubSubRequest(body: PubSubSlackSlashCommandMessage): Either<Throwable, Unit> = either {
        analytics.sendEvent(
            clientId = body.userId,
            name = "slack_slash_command",
            params = mapOf(
                "command" to body.command,
                "text" to body.text,
                "user_id" to body.userId,
                "team_id" to body.teamId,
            )
        )

        // Let the user know we've started searching
        slackRepository.postMessageToUrl(
            url = body.responseUrl,
            message = slackMessageFactory.searchingMessage(),
        ).bind()

        // Perform the search
        val searchEither = slackSearchRepository.search(body.text)

        // If an error happens, show it to the user
        val searchError = searchEither.leftOrNull()
        if (searchError != null) {
            analytics.sendEvent(
                clientId = body.userId,
                name = "slack_slash_command_error",
                params = searchError.message?.let {
                    mapOf(
                        "type" to "generic",
                        "info" to it,
                    )
                }
            )

            slackRepository.postMessageToUrl(
                url = body.responseUrl,
                message = slackMessageFactory.searchGenericErrorMessage()
            ).bind()

            return@either
        }

        // This should not error
        val searchResult = searchEither.getOrNull()!!
        val searchResultError = searchResult.error

        // Handle any search-specific outcomes, like NoResults
        if (searchResultError != null) {
            when (searchResultError) {
                is SlackSearchRepository.SearchResultDto.Error.NoResults -> {
                    analytics.sendEvent(
                        clientId = body.userId,
                        name = "slack_slash_command_error",
                        params = mapOf("type" to "no_results")
                    )

                    slackRepository.postMessageToUrl(
                        url = body.responseUrl,
                        message = slackMessageFactory.noSearchResultsMessage(body.text)
                    ).bind()
                }
            }
            return@either
        }

        val searchSession = searchResult.searchSession

        // No search session at this point is likely an error, so report it to the user
        if (searchSession == null) {
            analytics.sendEvent(
                clientId = body.userId,
                name = "slack_slash_command_error",
                params = mapOf("type" to "generic")
            )

            slackRepository.postMessageToUrl(
                url = body.responseUrl,
                message = slackMessageFactory.searchGenericErrorMessage()
            ).bind()

            return@either
        }

        // Finally, send the search result
        analytics.sendEvent(
            clientId = body.userId,
            name = "slack_slash_command_success",
            params = mapOf(
                "query" to searchSession.post.searchQuery,
                "post_title" to searchSession.post.attachmentTitle,
            ),
        )

        slackRepository.postMessageToUrl(
            url = body.responseUrl,
            message = slackMessageFactory.searchResultMessage(
                searchQuery = searchSession.post.searchQuery,
                searchResults = searchSession.searchResults,
                searchSessionId = searchSession.searchSessionId,
                attachmentTitle = searchSession.post.attachmentTitle,
                attachmentUrl = searchSession.post.attachmentUrl,
                attachmentImageUrl = searchSession.post.attachmentImageUrl,
            )
        ).bind()
    }
}
