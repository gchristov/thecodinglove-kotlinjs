package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.common.network.http.sendEmpty

interface PubSubDispatchHandler : HttpHandler {
    val pubSubDecoder: PubSubDecoder

    suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit>

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        val pubSubRequest = pubSubDecoder.decode(request).bind()
        handlePubSubRequest(pubSubRequest).bind()
        response.sendEmpty().bind()
    }
}
