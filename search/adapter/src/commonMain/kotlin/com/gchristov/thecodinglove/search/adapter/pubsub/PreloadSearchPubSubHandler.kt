package com.gchristov.thecodinglove.search.adapter.pubsub

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.search.adapter.pubsub.model.PubSubPreloadSearchMessage
import com.gchristov.thecodinglove.search.domain.usecase.PreloadSearchResultUseCase
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

    override suspend fun handlePubSubRequest(
        request: PubSubRequest
    ): Either<Throwable, Unit> = either {
        val body = request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = PubSubPreloadSearchMessage.serializer(),
        ).getOrNull()
        if (body == null) {
            // Swallow but report the error, so that we can investigate. Retrying is unlikely to help,
            // if the request body cannot be parsed.
            log.error(tag, Exception("Request body is invalid")) { "Error handling request" }
            return@either
        }

        preloadSearchResultUseCase(PreloadSearchResultUseCase.Dto(searchSessionId = body.searchSessionId))
            .mapLeft {
                // Swallow but report the error, so that we can investigate. Preload errors should not retry if the
                // PubSub body cannot be parsed, or we get any of the search errors, which are currently Exhausted,
                // and Empty, where retrying doesn't really make sense.
                log.error(tag, it) { "Error handling request" }
                Either.Right(Unit)
            }
    }
}
