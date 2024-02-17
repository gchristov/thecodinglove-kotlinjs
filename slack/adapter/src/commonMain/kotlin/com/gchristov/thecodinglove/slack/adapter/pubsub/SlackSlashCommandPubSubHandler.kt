package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.proto.pubsub.PubSubSlackSlashCommandMessage
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSlashCommandPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackSearchRepository: SlackSearchRepository,
    pubSubDecoder: PubSubDecoder,
) : BasePubSubHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
    pubSubDecoder = pubSubDecoder,
) {
    private val tag = this::class.simpleName

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/slash",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = PubSubSlackSlashCommandMessage.serializer(),
        )
            .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
            .flatMap { it.handle() }

    /*
     * This method handles errors as success without PubSub retries, but tries to notify the user of the error. If
     * sending the Slack reply back fails, the entire PubSub chain will be retried automatically. This is to avoid
     * unnecessary PubSub retries which would most likely result in additional errors if the problem is on our end.
     */
    private suspend fun PubSubSlackSlashCommandMessage.handle() = slackRepository.postMessageToUrl(
        url = responseUrl,
        message = slackMessageFactory.message("🔎 Hang tight, we're finding your GIF...")
    )
        .flatMap { slackSearchRepository.search(text) }
        .fold(
            ifLeft = {
                slackRepository.postMessageToUrl(
                    url = responseUrl,
                    message = slackMessageFactory.message(text = "⚠️ Something has gone wrong. We have been notified, so please try again while we investigate.")
                )
            },
            ifRight = { searchResult ->
                val searchSession = searchResult.searchSession
                when {
                    searchResult.ok && searchSession != null -> slackRepository.postMessageToUrl(
                        url = responseUrl,
                        message = slackMessageFactory.searchResultMessage(
                            searchQuery = searchSession.post.searchQuery,
                            searchResults = searchSession.searchResults,
                            searchSessionId = searchSession.searchSessionId,
                            attachmentTitle = searchSession.post.attachmentTitle,
                            attachmentUrl = searchSession.post.attachmentUrl,
                            attachmentImageUrl = searchSession.post.attachmentImageUrl,
                        )
                    )

                    else -> {
                        slackRepository.postMessageToUrl(
                            url = responseUrl,
                            message = slackMessageFactory.message(text = "No results found for '$text'. Please try a different search query, for example `release`.")
                        )
                    }
                }
            }
        )
}
