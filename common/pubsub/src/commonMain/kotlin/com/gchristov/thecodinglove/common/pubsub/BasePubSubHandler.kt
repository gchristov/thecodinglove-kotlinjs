package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
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
     * the PubSub message has been handled.,
     */
    @ExperimentalEncodingApi
    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        return either {
            val pubSubRequest = pubSubDecoder.decode(request).bind()
            handlePubSubRequest(pubSubRequest).bind()
            response.sendEmpty().bind()
        }
    }
}