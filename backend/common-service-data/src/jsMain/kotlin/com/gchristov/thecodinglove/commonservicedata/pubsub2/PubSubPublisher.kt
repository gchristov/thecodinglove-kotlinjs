package com.gchristov.thecodinglove.commonservicedata.pubsub2

import arrow.core.Either
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

interface PubSubPublisher {
    /**
     * @return The sent PubSub message ID as an [Either]
     */
    suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: Json,
        strategy: SerializationStrategy<T>,
    ): Either<Throwable, String>
}