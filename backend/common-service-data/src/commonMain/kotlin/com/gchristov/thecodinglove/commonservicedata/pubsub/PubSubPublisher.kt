package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommonkotlin.JsonSerializer
import kotlinx.serialization.SerializationStrategy

interface PubSubPublisher {
    /**
     * @return The sent PubSub message ID as an [Either]
     */
    suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
    ): Either<Throwable, String>
}