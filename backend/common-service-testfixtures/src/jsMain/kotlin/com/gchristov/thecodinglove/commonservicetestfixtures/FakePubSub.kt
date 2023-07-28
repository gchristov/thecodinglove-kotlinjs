package com.gchristov.thecodinglove.commonservicetestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSub
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubSubscription
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubSubscriptionOptions
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubTopic
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import kotlin.test.assertEquals

class FakePubSub : PubSub {
    private val pubSubTopic = FakePubSubTopic()
    private var lastTopic: String? = null

    override fun topic(name: String): PubSubTopic {
        lastTopic = name
        return pubSubTopic
    }

    fun assertEquals(
        topic: String?,
        message: Buffer?
    ) {
        assertEquals(
            expected = topic,
            actual = lastTopic,
        )
        pubSubTopic.assertEquals(message)
    }

    fun assertNotInvoked() = pubSubTopic.assertNotInvoked()
}

private class FakePubSubTopic : PubSubTopic {
    private var lastMessage: Buffer? = null
    private var publishInvocations = 0

    override suspend fun exists(): Either<Throwable, Boolean> = Either.Right(true)

    override suspend fun create(): Either<Throwable, PubSubTopic> = Either.Right(this)

    override fun subscription(name: String): PubSubSubscription = FakePubSubSubscription()

    override suspend fun publish(message: Buffer): Either<Throwable, String> {
        publishInvocations++
        lastMessage = message
        return Either.Right("topic_123")
    }

    fun assertEquals(message: Buffer?) {
        assertEquals(
            expected = lastMessage,
            actual = message,
        )
    }

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = publishInvocations
        )
    }
}

private class FakePubSubSubscription : PubSubSubscription {
    override suspend fun exists(): Either<Throwable, Boolean> = Either.Right(true)

    override suspend fun create(options: PubSubSubscriptionOptions?): Either<Throwable, PubSubSubscription> =
        Either.Right(this)
}