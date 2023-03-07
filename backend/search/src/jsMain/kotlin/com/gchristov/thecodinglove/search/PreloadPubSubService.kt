package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.decodeBodyFromJson
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubTopic
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import kotlinx.serialization.json.Json

class PreloadPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase
) : PubSubService(
    pubSubServiceRegister = pubSubServiceRegister,
    log = log,
) {
    override fun topic(): String = PreloadPubSubTopic

    override fun register() {
        exports.preloadPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> =
        message.decodeBodyFromJson<PreloadPubSubMessage>(
            jsonSerializer = jsonSerializer,
            log = log
        )
            .leftIfNull(default = { Exception("Message body is null") })
            .flatMap { preloadSearchResultUseCase(searchSessionId = it.searchSessionId) }
}