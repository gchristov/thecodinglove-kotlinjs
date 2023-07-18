package com.gchristov.thecodinglove.commonservice.pubsub

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.BaseHttpHandler
import com.gchristov.thecodinglove.commonservice.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.decodeBodyFromJson
import com.gchristov.thecodinglove.commonservicedata.http.sendEmpty
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSub
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.json

interface PubSubHttpHandler : HttpHandler {
    fun pubSubConfig(): Config

    data class Config(
        val topic: String,
        val subscription: String,
    )
}

abstract class BasePubSubHttpHandler(
    private val jsonSerializer: Json,
    private val log: Logger,
    private val pubSub: PubSub,
) : BaseHttpHandler(
    jsonSerializer = jsonSerializer,
    log = log,
), PubSubHttpHandler {
    override suspend fun initialise(): Either<Throwable, Unit> {
        val pubSubConfig = pubSubConfig()
        val httpConfig = httpConfig()
        val topic = pubSub.topic(pubSubConfig.topic)
        val subscription = topic.subscription(pubSubConfig.subscription)
        return super.initialise().flatMap {
            topic
                .exists()
                .flatMap { exists ->
                    if (!exists) {
                        log.d("Creating PubSub topic ${pubSubConfig.topic}")
                        topic.create()
                    } else {
                        Either.Right(Unit)
                    }
                }
                .flatMap { subscription.exists() }
                .flatMap { exists ->
                    if (!exists) {
                        log.d("Creating PubSub topic subscription ${pubSubConfig.subscription}")
                        subscription.create(
                            // TODO: Fix this to use a env variable for the website
                            json("pushEndpoint" to "https://codinglove.serveo.net${httpConfig.path}")
                        )
                    } else {
                        Either.Right(Unit)
                    }
                }
                .flatMap { Either.Right(Unit) }
        }
    }

    abstract suspend fun handlePubSubAsync(dataJson: String): Either<Throwable, Unit>

    /**
     * Decodes and exposes the PubSub message to subclasses. A success response is always sent to the [response] after
     * the PubSub message has been handled.,
     */
    @ExperimentalEncodingApi
    override suspend fun handleAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        return request.decodeBodyFromJson<PubSubRequest>(jsonSerializer)
            .flatMap { pubSubRequest ->
                pubSubRequest?.let {
                    val decodedData = Base64.decode(it.message.dataBase64).decodeToString()
                    handlePubSubAsync(decodedData).flatMap {
                        response.sendEmpty()
                    }
                } ?: Either.Left(Exception("PubSub body missing"))
            }
    }
}

@Serializable
private data class PubSubRequest(val message: Message) {
    @Serializable
    data class Message(
        @SerialName("data")
        val dataBase64: String
    )
}