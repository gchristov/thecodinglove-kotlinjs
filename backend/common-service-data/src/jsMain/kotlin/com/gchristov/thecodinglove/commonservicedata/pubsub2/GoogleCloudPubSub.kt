package com.gchristov.thecodinglove.commonservicedata.pubsub2

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import com.gchristov.thecodinglove.kmpcommonkotlin.process
import kotlinx.coroutines.await
import kotlin.js.Json
import kotlin.js.Promise

internal class GoogleCloudPubSub : PubSub {
    private val pubSub = GoogleCloudPubSubDefinition.PubSub()

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-pubsub.json"
    }

    override fun topic(name: String): PubSubTopic = GoogleCloudPubSubTopic(
        topic = pubSub.topic(name),
    )
}

internal class GoogleCloudPubSubTopic(
    private val topic: GoogleCloudPubSubDefinition.Topic
) : PubSubTopic {
    override suspend fun exists(): Either<Throwable, Boolean> = try {
        val result = topic.exists().await().first()
        Either.Right(result)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error finding PubSub topic${error.message?.let { ": $it" } ?: ""}",
            cause = error
        ))
    }

    override suspend fun create(): Either<Throwable, PubSubTopic> = try {
        topic.create().await()
        Either.Right(this)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error creating PubSub topic${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override fun subscription(name: String): PubSubSubscription = GoogleCloudPubSubSubscription(
        subscription = topic.subscription(name),
    )

    override suspend fun publish(message: Buffer): Either<Throwable, String> = try {
        val result = topic.publish(message).await()
        Either.Right(result)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error publishing PubSub message${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

internal class GoogleCloudPubSubSubscription(
    private val subscription: GoogleCloudPubSubDefinition.Subscription
) : PubSubSubscription {
    override suspend fun exists(): Either<Throwable, Boolean> = try {
        val result = subscription.exists().await().first()
        Either.Right(result)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error finding PubSub subscription${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun create(options: Json?): Either<Throwable, PubSubSubscription> = try {
        subscription.create(options).await()
        Either.Right(this)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error creating PubSub subscription${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

@JsModule("@google-cloud/pubsub")
@JsNonModule
internal external object GoogleCloudPubSubDefinition {

    class PubSub {
        fun topic(name: String): Topic
    }

    class Topic {
        fun exists(): Promise<Array<Boolean>>

        fun create(): Promise<Topic>

        fun subscription(name: String): Subscription

        fun publish(message: Buffer): Promise<String>
    }

    class Subscription {
        fun exists(): Promise<Array<Boolean>>

        fun create(options: Json?): Promise<Subscription>
    }
}