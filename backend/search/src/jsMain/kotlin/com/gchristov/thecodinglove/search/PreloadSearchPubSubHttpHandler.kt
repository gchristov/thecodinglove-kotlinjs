package com.gchristov.thecodinglove.search

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.HttpHandler
import com.gchristov.thecodinglove.commonservice.pubsub.BasePubSubHttpHandler
import com.gchristov.thecodinglove.commonservice.pubsub.PubSubHttpHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSub
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import io.ktor.http.*
import kotlinx.serialization.json.Json

class PreloadSearchPubSubHttpHandler(
    private val jsonSerializer: Json,
    private val log: Logger,
    private val searchUseCase: SearchUseCase,
    private val pubSub: PubSub,
) : BasePubSubHttpHandler(
    jsonSerializer = jsonSerializer,
    log = log,
    pubSub = pubSub,
) {
    override fun httpConfig() = HttpHandler.Config(
        method = HttpMethod.Post,
        path = "/pubsub/notifications",
        contentType = ContentType.Application.Json,
    )

    override fun pubSubConfig() = PubSubHttpHandler.Config(
        topic = PubSubTopic,
        subscription = PubSubSubscription,
    )

    override suspend fun handlePubSubAsync(dataJson: String): Either<Throwable, Unit> {
        println("PUBSUB_JSON=$dataJson")
        return Either.Right(Unit)
    }

    companion object {
        const val PubSubTopic = "test-4"
        private const val PubSubSubscription = "test-sub-4"
    }
}