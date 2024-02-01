package com.gchristov.thecodinglove.common.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
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