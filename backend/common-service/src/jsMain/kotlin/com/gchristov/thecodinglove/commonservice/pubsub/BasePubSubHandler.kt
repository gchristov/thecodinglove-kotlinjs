package com.gchristov.thecodinglove.commonservice.pubsub

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.BaseHttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.sendEmpty
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubSubscription
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import kotlin.io.encoding.ExperimentalEncodingApi

abstract class BasePubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
    log: Logger,
    private val pubSubSubscription: PubSubSubscription,
) : BaseHttpHandler(
    dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
), PubSubHandler {
    override suspend fun initialise(): Either<Throwable, Unit> {
        val pubSubConfig = pubSubConfig()
        val httpConfig = httpConfig()
        return super.initialise().flatMap {
            pubSubSubscription.initialise(
                topic = pubSubConfig.topic,
                httpPath = httpConfig.path,
            )
        }
    }

    /**
     * Decodes and exposes the PubSub message to subclasses. A success response is always sent to the [response] after
     * the PubSub message has been handled.,
     */
    @ExperimentalEncodingApi
    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        return request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = GoogleCloudPubSubRequestBody.serializer(),
        )
            .leftIfNull { Exception("PubSub request body missing") }
            .flatMap { it.toPubSubRequest() }
            .flatMap { handlePubSubRequest(it) }
            .flatMap { response.sendEmpty() }
    }
}