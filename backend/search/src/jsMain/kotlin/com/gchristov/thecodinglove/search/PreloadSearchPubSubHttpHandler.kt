package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub2.*
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubSubscription
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubTopic
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import kotlin.io.encoding.ExperimentalEncodingApi

class PreloadSearchPubSubHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
    log: Logger,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase,
    pubSub: PubSub,
) : BasePubSubHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
    pubSub = pubSub,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/pubsub/notifications",
        contentType = ContentType.Application.Json,
    )

    override fun pubSubConfig() = PubSubHttpHandler.PubSubConfig(
        topic = PreloadSearchPubSubTopic,
        subscription = PreloadSearchPubSubSubscription,
    )

    @ExperimentalEncodingApi
    override suspend fun handlePubSubRequestAsync(request: PubSubRequest): Either<Throwable, Unit> =
        request.decodeMessageFromJson<PreloadSearchPubSubMessage>(jsonSerializer)
            .flatMap { preloadSearchResultUseCase(searchSessionId = it.searchSessionId) }
}