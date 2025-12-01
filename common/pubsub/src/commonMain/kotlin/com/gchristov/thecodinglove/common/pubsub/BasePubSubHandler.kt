package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.BaseHttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.common.network.http.sendEmpty
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.io.encoding.ExperimentalEncodingApi

abstract class BasePubSubHandler<T>(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val pubSubDecoder: PubSubDecoder,
) : BaseHttpHandler(
    dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
), PubSubHandler<T> {
    private val tag = this::class.simpleName

    /**
     * Decodes and exposes the PubSub message to subclasses. A success response is always sent to the [response] after
     * the PubSub message has been successfully handled.
     */
    @ExperimentalEncodingApi
    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        // Decode the PubSub request
        val decodeRequestEither = pubSubDecoder.decode(request)
        val decodeRequestError = decodeRequestEither.leftOrNull()
        if (decodeRequestError != null) {
            // Swallow but report the error, so that we can investigate. Retrying is unlikely to help,
            // if the request cannot be decoded.
            log.error(tag, decodeRequestError) { "Error decoding PubSub request" }
            return@either
        }

        // This should not be null here
        val decodedRequest = decodeRequestEither.getOrNull()!!

        // Decode the PubSub message body
        val decodeBodyEither = decodedRequest.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = deserialisationStrategy(),
        )
        val decodeBodyError = decodeBodyEither.leftOrNull()
        if (decodeBodyError != null) {
            // Swallow but report the error, so that we can investigate. Retrying is unlikely to help,
            // if the request body cannot be parsed.
            log.error(tag, decodeBodyError) { "Error decoding PubSub message body" }
            return@either
        }

        // This should not be null here
        val decodedBody = decodeBodyEither.getOrNull()!!

        handlePubSubRequest(decodedBody).bind()

        // If no errors have happened, acknowledge the PubSub message as handled
        response.sendEmpty().bind()
    }
}