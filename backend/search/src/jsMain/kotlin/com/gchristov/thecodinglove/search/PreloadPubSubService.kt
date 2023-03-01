package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.bodyAsJson
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import kotlinx.serialization.json.Json

class PreloadPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    log: Logger,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase
) : PubSubService(
    pubSubServiceRegister = pubSubServiceRegister,
    log = log,
) {
    override fun topic(): String = Topic

    override fun register() {
        exports.preloadPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> =
        message.bodyAsJson<PreloadPubSubMessage>(jsonSerializer)
            .leftIfNull(default = { Exception("Message body is null") })
            .flatMap { preloadSearchResultUseCase(searchSessionId = it.searchSessionId) }

    companion object {
        const val Topic = "preloadPubSub"
    }
}