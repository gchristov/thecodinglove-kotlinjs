package com.gchristov.thecodinglove.common.adaptertestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.adapter.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import kotlinx.serialization.SerializationStrategy
import kotlin.test.assertEquals

class FakePubSubPublisher : PubSubPublisher {
    private var lastTopic: String? = null
    private var lastBody: Any? = null
    private var publishInvocations = 0

    override suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>
    ): Either<Throwable, String> {
        lastTopic = topic
        lastBody = body
        publishInvocations++
        return Either.Right("message_123")
    }

    fun assertEquals(
        topic: String?,
        message: Any?,
    ) {
        assertEquals(
            expected = topic,
            actual = lastTopic,
        )
        assertEquals(
            expected = message,
            actual = lastBody,
        )
    }

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = publishInvocations
        )
    }
}