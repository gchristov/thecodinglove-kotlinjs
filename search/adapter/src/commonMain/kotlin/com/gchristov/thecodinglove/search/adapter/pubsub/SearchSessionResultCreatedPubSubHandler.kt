package com.gchristov.thecodinglove.search.adapter.pubsub

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubEventHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SearchSessionResultCreatedPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val eventHandlers: List<PubSubEventHandler<SearchSessionResultCreatedEvent>>,
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
        path = "/api/pubsub/search/session-result-created",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        either {
            val body = request.decodeBodyFromJson(
                jsonSerializer = jsonSerializer,
                strategy = SearchSessionResultCreatedEvent.serializer(),
            ).bind() ?: raise(Exception("Request body is invalid"))
            eventHandlers.firstOrNull { it.canHandle(body) }?.handle(body)?.bind()
            Unit
        }.fold(
            // Swallow but report the error, so that we can investigate. Preload errors should not retry if the
            // PubSub body cannot be parsed, or we get any of the search errors, which are currently Exhausted,
            // Empty and NotFound, where retrying doesn't really make sense for any of them.
            ifLeft = { log.error(tag, it) { "Error handling request" }; Either.Right(Unit) },
            ifRight = { Either.Right(Unit) },
        )
}
