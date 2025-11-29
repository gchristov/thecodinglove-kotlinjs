package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.BaseHttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.common.network.http.sendEmpty
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.io.encoding.ExperimentalEncodingApi

abstract class BasePubSubHandler(
    dispatcher: CoroutineDispatcher,
    jsonSerializer: JsonSerializer,
    log: Logger,
    private val pubSubDecoder: PubSubDecoder,
) : BaseHttpHandler(
    dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
), PubSubHandler {
    /**
     * Decodes and exposes the PubSub message to subclasses. A success response is always sent to the [response] after
     * the PubSub message has been successfully handled.
     */
    @ExperimentalEncodingApi
    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        val decodedRequest = pubSubDecoder.decode(request).bind()
        handlePubSubRequest(decodedRequest).bind()
        response.sendEmpty().bind()
    }
}