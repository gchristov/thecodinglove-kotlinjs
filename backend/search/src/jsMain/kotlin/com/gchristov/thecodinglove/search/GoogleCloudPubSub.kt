package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.PubSub
import com.gchristov.thecodinglove.commonservice.PubSubSubscription
import com.gchristov.thecodinglove.commonservice.PubSubTopic
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import com.gchristov.thecodinglove.kmpcommonkotlin.process
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class GoogleCloudPubSub : PubSub {
    private val pubSub: dynamic = js("new (require(\"@google-cloud/pubsub\")).PubSub()")

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-pubsub.json"
    }

    override fun topic(name: String): PubSubTopic = GoogleCloudPubSubTopic(pubSub.topic(name))
}

internal class GoogleCloudPubSubTopic(private val topic: dynamic) : PubSubTopic {
    override suspend fun publishMessage(message: Buffer): Either<Throwable, String> {
        TODO("Not yet implemented")
    }

    override suspend fun exists(): Either<Throwable, Boolean> = coroutineScope {
        try {
            val result = convertToKotlinPromise(topic.exists())
            println(js("typeof result"))
            Either.Right(false)
        } catch (e: Throwable) {
            Either.Left(e)
        }
    }

    override suspend fun create(): Either<Throwable, Unit> = coroutineScope {
        try {
            val result = convertToKotlinPromise(topic.create())
            Either.Right(result)
        } catch (e: Throwable) {
            Either.Left(e)
        }
    }

    override fun subscription(name: String): PubSubSubscription =
        GoogleCloudPubSubSubscription(topic.subscription(name))
}

internal class GoogleCloudPubSubSubscription(private val subscription: dynamic) : PubSubSubscription {
    override suspend fun exists(): Either<Throwable, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun create(pushEndpoint: String): Either<Throwable, Unit> {
        TODO("Not yet implemented")
    }
}

suspend fun convertToKotlinPromise(jsPromise: dynamic): dynamic = suspendCoroutine { continuation ->
    jsPromise.then(
        { result ->
            continuation.resume(result)
        },
        { error ->
            continuation.resumeWithException(error)
        }
    )
    Unit
}