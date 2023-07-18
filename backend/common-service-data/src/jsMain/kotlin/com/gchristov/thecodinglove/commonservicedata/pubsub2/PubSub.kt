package com.gchristov.thecodinglove.commonservicedata.pubsub2

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface PubSub {
    fun topic(name: String): PubSubTopic
}

interface PubSubTopic {
    suspend fun exists(): Either<Throwable, Boolean>

    suspend fun create(): Either<Throwable, PubSubTopic>

    fun subscription(name: String): PubSubSubscription

    suspend fun publish(message: Buffer): Either<Throwable, String>
}

interface PubSubSubscription {
    suspend fun exists(): Either<Throwable, Boolean>

    suspend fun create(options: PubSubSubscriptionOptions?): Either<Throwable, PubSubSubscription>
}

suspend inline fun <reified T> PubSubTopic.publish(
    body: T,
    jsonSerializer: Json,
): Either<Throwable, String> = try {
    val jsonString = jsonSerializer.encodeToString(body)
    publish(Buffer.from(jsonString))
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "Error during PubSubTopic publish${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}

typealias PubSubSubscriptionOptions = kotlin.js.Json
