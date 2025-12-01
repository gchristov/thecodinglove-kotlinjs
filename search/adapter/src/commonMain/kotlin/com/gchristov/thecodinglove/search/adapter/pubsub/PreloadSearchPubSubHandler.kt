package com.gchristov.thecodinglove.search.adapter.pubsub

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
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
) : BasePubSubHandler<PubSubPreloadSearchMessage>(
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

    override fun deserialisationStrategy() = PubSubPreloadSearchMessage.serializer()

    override suspend fun handlePubSubRequest(
        body: PubSubPreloadSearchMessage
    ): Either<Throwable, Unit> = either {
        preloadSearchResultUseCase(PreloadSearchResultUseCase.Dto(searchSessionId = body.searchSessionId))
            .mapLeft {
                // Preload errors should not retry if we get a search error (Exhausted|Empty), as retrying doesn't
                // really make sense. In that case swallow but report the error, so that we can investigate.
                log.error(tag, it) { "Error handling request" }
                Either.Right(Unit)
            }
    }
}
