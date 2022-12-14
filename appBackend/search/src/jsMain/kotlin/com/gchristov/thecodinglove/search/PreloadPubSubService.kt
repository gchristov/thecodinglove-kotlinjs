package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.PubSubMessage
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservice.bodyFromJson
import com.gchristov.thecodinglove.commonservice.exports
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class PreloadPubSubService(
    private val jsonParser: Json,
    private val preloadSearchResultUseCase: PreloadSearchResultUseCase
) : PubSubService() {
    override fun register() {
        exports.preloadPubSub = registerForPubSubCallbacks("trigger")
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Exception, Unit> {
        println("Received request to pre-load search results")
        return try {
            val topicMessage = message.json.bodyFromJson<PreloadTopicMessage>(jsonParser)
            return preloadSearchResultUseCase(searchSessionId = topicMessage.searchSessionId)
        } catch (error: Exception) {
            error.printStackTrace()
            Either.Left(error)
        }
    }
}

@Serializable
data class PreloadTopicMessage(val searchSessionId: String)