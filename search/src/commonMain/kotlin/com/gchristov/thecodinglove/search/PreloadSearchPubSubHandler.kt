package com.gchristov.thecodinglove.search

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
import com.gchristov.thecodinglove.searchdata.domain.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class PreloadSearchPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase,
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
        path = "/api/pubsub/search",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = PreloadSearchPubSubMessage.serializer(),
        )
            .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
            .flatMap { preloadSearchResultUseCase(searchSessionId = it.searchSessionId) }
            .fold(
                ifLeft = {
                    // Swallow but report the error, so that we can investigate. Preload errors should not retry if the
                    // PubSub body cannot be parsed, or we get any of the search errors, which are currently Exhausted,
                    // Empty and NotFound, where retrying doesn't really make sense for any of them.
                    log.error(tag, it) { "Error handling request" }
                    Either.Right(Unit)
                }, ifRight = { Either.Right(Unit) }
            )
}
