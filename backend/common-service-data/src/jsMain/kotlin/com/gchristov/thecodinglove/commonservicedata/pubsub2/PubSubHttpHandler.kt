package com.gchristov.thecodinglove.commonservicedata.pubsub2

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.js.json

interface PubSubHttpHandler : HttpHandler {
    fun pubSubConfig(): PubSubConfig

    data class PubSubConfig(
        val topic: String,
        val subscription: String,
    )
}

abstract class BasePubSubHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val pubSub: PubSub,
) : BaseHttpHandler(
    dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
), PubSubHttpHandler {
    protected abstract suspend fun handlePubSubRequestAsync(request: PubSubRequest): Either<Throwable, Unit>

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
            deserializer = PubSubRequest.serializer(),
        )
            .flatMap { pubSubRequest ->
                pubSubRequest?.let {
                    handlePubSubRequestAsync(it).flatMap {
                        response.sendEmpty()
                    }
                } ?: Either.Left(Exception("PubSub body missing"))
            }
    }
}