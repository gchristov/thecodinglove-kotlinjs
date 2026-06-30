package com.gchristov.thecodinglove.search.adapter.pubsub

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.common.network.http.sendEmpty
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubHandler
import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent
import com.gchristov.thecodinglove.search.domain.usecase.PreloadSearchResultUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SearchPreloadPubSubHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    override val pubSubDecoder: PubSubDecoder,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase,
) : PubSubHandler<SearchSessionResultCreatedEvent> {
    private val tag = this::class.simpleName

    override val strategy = SearchSessionResultCreatedEvent.serializer()

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/search/preload",
        contentType = ContentType.Application.Json,
    )

    // Swallow all errors — preload failures and parse errors should not trigger PubSub retries.
    override suspend fun handleError(error: Throwable, response: HttpResponse): Either<Throwable, Unit> {
        log.error(tag, error) { "Error handling request" }
        return response.sendEmpty()
    }

    override suspend fun handle(event: SearchSessionResultCreatedEvent): Either<Throwable, Unit> {
        preloadSearchResultUseCase(PreloadSearchResultUseCase.Dto(searchSessionId = event.searchSessionId))
            .getOrElse {
                log.error(tag, it) { "Error preloading search" }
                return Either.Right(Unit)
            }
        return Either.Right(Unit)
    }
}
