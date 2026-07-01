package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import kotlinx.serialization.SerializationStrategy
import kotlin.time.Duration

interface PubSubPublisher {
    /**
     * @param delay When greater than [Duration.ZERO], the message is published after the delay
     * has elapsed instead of immediately.
     * @return The sent PubSub message ID as an [Either]
     */
    suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
        delay: Duration = Duration.ZERO,
    ): Either<Throwable, String>
}