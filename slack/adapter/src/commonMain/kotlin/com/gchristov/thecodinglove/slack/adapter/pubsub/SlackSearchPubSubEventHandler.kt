package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.pubsub.PubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandReceivedEvent
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

internal class SlackSearchPubSubEventHandler(
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackSearchRepository: SlackSearchRepository,
    private val analytics: Analytics,
    private val slashCommand: String,
) : PubSubEventHandler<SlackSlashCommandReceivedEvent> {
    /*
     * Handles errors as success without PubSub retries, but tries to notify the user of the error. If sending
     * the Slack reply back fails, the entire PubSub chain will be retried automatically. This is to avoid
     * unnecessary PubSub retries which would most likely result in additional errors if the problem is on our end.
     */
    override suspend fun handle(event: SlackSlashCommandReceivedEvent): Either<Throwable, Unit> {
        if (event.command != slashCommand) return Either.Right(Unit)
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
                        params = error.message?.let {
                            mapOf(
                                "type" to "generic",
                                "info" to it,
                            )
                        }
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
