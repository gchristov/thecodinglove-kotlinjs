package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.searchdata.domain.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.searchdata.domain.SearchConfig
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

class PreloadSearchPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
    log: Logger,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase,
    pubSubSubscription: PubSubSubscription,
    private val searchConfig: SearchConfig,
) : BasePubSubHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
    pubSubSubscription = pubSubSubscription,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/search",
        contentType = ContentType.Application.Json,
    )

    override fun pubSubConfig() = PubSubHandler.PubSubConfig(
        topic = searchConfig.preloadPubSubTopic,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = PreloadSearchPubSubMessage.serializer(),
        )
            .leftIfNull { Exception("PubSub request body missing") }
            .flatMap { preloadSearchResultUseCase(searchSessionId = it.searchSessionId) }
}