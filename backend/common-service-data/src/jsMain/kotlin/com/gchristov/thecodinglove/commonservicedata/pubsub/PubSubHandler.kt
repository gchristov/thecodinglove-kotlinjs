package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.Handler

interface PubSubHandler : Handler {

    fun pubSubConfig(): PubSubConfig

    suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit>

    data class PubSubConfig(val topic: String)
}