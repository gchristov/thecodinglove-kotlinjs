package com.gchristov.thecodinglove.common.pubsubtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import kotlinx.serialization.SerializationStrategy
import kotlin.test.assertEquals
import kotlin.time.Duration

class FakePubSubPublisher(
    private val publishResult: Either<Throwable, String> = Either.Right("message_123"),
) : PubSubPublisher {
    private var lastTopic: String? = null
    private var lastBody: Any? = null
    private var lastDelay: Duration? = null
    private var publishInvocations = 0

    override suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
        delay: Duration,
    ): Either<Throwable, String> {
        lastTopic = topic
        lastBody = body
        lastDelay = delay
        publishInvocations++
        return publishResult
    }

    fun assertEquals(
        topic: String?,
        message: Any?,
        delay: Duration = Duration.ZERO,
    ) {
        assertEquals(
            expected = topic,
            actual = lastTopic,
        )
        assertEquals(
            expected = message,
            actual = lastBody,
        )
        assertEquals(
            expected = delay,
            actual = lastDelay,
        )
    }

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = publishInvocations
        )
    }

    // For cases where the body/delay aren't practical to assert exactly (e.g. delay computed from
    // a real wall-clock read) - just confirms a publish happened for the given topic.
    fun assertTopic(topic: String) {
        assertEquals(
            expected = topic,
            actual = lastTopic,
        )
    }
}