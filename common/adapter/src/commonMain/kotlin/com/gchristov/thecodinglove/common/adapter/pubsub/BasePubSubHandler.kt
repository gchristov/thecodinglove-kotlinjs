package com.gchristov.thecodinglove.common.adapter.pubsub

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.adapter.http.BaseHttpHandler
import com.gchristov.thecodinglove.common.adapter.http.HttpRequest
import com.gchristov.thecodinglove.common.adapter.http.HttpResponse
import com.gchristov.thecodinglove.common.adapter.http.sendEmpty
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
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
     * the PubSub message has been handled.,
     */
    @ExperimentalEncodingApi
    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        return pubSubDecoder.decode(request)
            .flatMap { handlePubSubRequest(it) }
            .flatMap { response.sendEmpty() }
    }
}