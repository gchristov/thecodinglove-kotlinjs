package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.common.network.http.sendEmpty
import kotlinx.serialization.DeserializationStrategy

interface PubSubHandler<T> : HttpHandler {
    val pubSubDecoder: PubSubDecoder
    val strategy: DeserializationStrategy<T>

    suspend fun handle(event: T): Either<Throwable, Unit>

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        val pubSubRequest = pubSubDecoder.decode(request).bind()
        val body = pubSubRequest.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = strategy,
        ).bind() ?: raise(Exception("Request body is invalid"))
        handle(body).bind()
        response.sendEmpty().bind()
    }
}
