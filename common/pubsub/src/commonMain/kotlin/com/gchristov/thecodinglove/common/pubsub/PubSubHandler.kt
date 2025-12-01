package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.http.Handler
import kotlinx.serialization.DeserializationStrategy

interface PubSubHandler<T> : Handler {
    fun deserialisationStrategy(): DeserializationStrategy<T>
    suspend fun handlePubSubRequest(body: T): Either<Throwable, Unit>
}