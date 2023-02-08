package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.bodyAsJson
import com.gchristov.thecodinglove.search.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage
import kotlinx.serialization.json.Json

class PreloadPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase
) : PubSubService(pubSubServiceRegister = pubSubServiceRegister) {
    override fun topic(): String = Topic

    override fun register() {
        exports.preloadPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> {
        return try {
            val topicMessage =
                requireNotNull(message.bodyAsJson<PreloadPubSubMessage>(jsonSerializer))
            preloadSearchResultUseCase(searchSessionId = topicMessage.searchSessionId)
        } catch (error: Throwable) {
            Either.Left(error)
        }
    }

    companion object {
        fun buildPubSubMessage(searchSessionId: String) = PreloadPubSubMessage(
            topic = Topic,
            searchSessionId = searchSessionId
        )
    }
}

private const val Topic = "preloadPubSub"