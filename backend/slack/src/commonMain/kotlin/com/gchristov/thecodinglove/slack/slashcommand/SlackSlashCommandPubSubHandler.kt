package com.gchristov.thecodinglove.slack.slashcommand

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.error
import com.gchristov.thecodinglove.commonservice.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSlashCommandPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val searchUseCase: SearchUseCase,
    pubSubDecoder: PubSubDecoder,
    private val slackConfig: SlackConfig,
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
        message = ApiSlackMessageFactory.message("🔎 Hang tight, we're finding your GIF...")
    )
        .flatMap { searchUseCase(SearchUseCase.Type.NewSession(query = text)) }
        .flatMap { searchResult ->
            slackRepository.postMessageToUrl(
                url = responseUrl,
                message = ApiSlackMessageFactory.searchResultMessage(
                    searchQuery = searchResult.query,
                    searchResults = searchResult.totalPosts,
                    searchSessionId = searchResult.searchSessionId,
                    attachmentTitle = searchResult.post.title,
                    attachmentUrl = searchResult.post.url,
                    attachmentImageUrl = searchResult.post.imageUrl
                )
            )
        }
        .fold(
            ifLeft = {
                // Handle errors as success without PubSub retries, but try to notify the user of the error. If sending
                // the reply back fails, the entire PubSub chain will be retried automatically.
                log.error(tag, it) { "Error handling request" }
                val userErrorMessage = when {
                    it is SearchError.Empty -> "No results found for '$text'. Please try a different search query."
                    else -> "⚠️ Something has gone wrong. Please try again while we investigate."
                }
                slackRepository.postMessageToUrl(
                    url = responseUrl,
                    message = ApiSlackMessageFactory.message(text = userErrorMessage)
                )
            },
            ifRight = { Either.Right(Unit) }
        )
}
