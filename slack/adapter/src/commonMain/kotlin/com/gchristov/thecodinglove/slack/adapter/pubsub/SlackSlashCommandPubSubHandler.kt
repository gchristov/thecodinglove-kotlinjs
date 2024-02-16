package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandPubSubMessage
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SearchRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSlashCommandPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val searchRepository: SearchRepository,
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
            strategy = SlackSlashCommandPubSubMessage.serializer(),
        )
            .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
            .flatMap { it.handle() }

    private suspend fun SlackSlashCommandPubSubMessage.handle() = slackRepository.postMessageToUrl(
        url = responseUrl,
        message = slackMessageFactory.message("🔎 Hang tight, we're finding your GIF...")
    )
        .flatMap { searchRepository.search(text) }
        .flatMap { searchResult ->
            slackRepository.postMessageToUrl(
                url = responseUrl,
                message = slackMessageFactory.searchResultMessage(
                    searchQuery = searchResult.searchQuery,
                    searchResults = searchResult.searchResults,
                    searchSessionId = searchResult.searchSessionId,
                    attachmentTitle = searchResult.attachmentTitle,
                    attachmentUrl = searchResult.attachmentUrl,
                    attachmentImageUrl = searchResult.attachmentImageUrl,
                )
            )
        }
        .fold(
            ifLeft = {
                // Handle errors as success without PubSub retries, but try to notify the user of the error. If sending
                // the reply back fails, the entire PubSub chain will be retried automatically.
                log.error(tag, it) { "Error handling request" }
                val userErrorMessage = when {
//                    it is SearchError.Empty -> "No results found for '$text'. Please try a different search query."
                    else -> "⚠️ Something has gone wrong. Please try again while we investigate."
                }
                slackRepository.postMessageToUrl(
                    url = responseUrl,
                    message = slackMessageFactory.message(text = userErrorMessage)
                )
            },
            ifRight = { Either.Right(Unit) }
        )
}
