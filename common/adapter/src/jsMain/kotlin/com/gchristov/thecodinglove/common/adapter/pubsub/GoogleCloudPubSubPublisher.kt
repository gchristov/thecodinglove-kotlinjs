package com.gchristov.thecodinglove.common.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.Buffer
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.process
import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy

internal class GoogleCloudPubSubPublisher(
    private val pubSub: GoogleCloudPubSubExternals.PubSub
) : PubSubPublisher {

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "credentials-gcp-app.json"
    }

    override suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>
    ): Either<Throwable, String> = try {
        val jsonString = jsonSerializer.json.encodeToString(strategy, body)
        val result = pubSub
            .topic(topic)
            .publish(Buffer.from(jsonString))
            .await()
        Either.Right(result)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error publishing PubSub JSON${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}