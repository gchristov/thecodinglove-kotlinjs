package com.gchristov.thecodinglove.commonservicetestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

class FakePubSubPublisher : PubSubPublisher {
    private var lastTopic: String? = null
    private var lastBody: Any? = null
    private var publishInvocations = 0

    override suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: Json,
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