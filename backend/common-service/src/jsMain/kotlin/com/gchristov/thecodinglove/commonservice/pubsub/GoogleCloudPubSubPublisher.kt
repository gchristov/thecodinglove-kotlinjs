package com.gchristov.thecodinglove.commonservice.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubPublisher
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import com.gchristov.thecodinglove.kmpcommonkotlin.process
import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

internal class GoogleCloudPubSubPublisher(
    private val pubSub: GoogleCloudPubSubExternals.PubSub
) : PubSubPublisher {

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-pubsub.json"
    }

    override suspend fun <T> publishJson(
        topic: String,
        body: T,
        jsonSerializer: Json,
        strategy: SerializationStrategy<T>
    ): Either<Throwable, String> = try {
        val jsonString = jsonSerializer.encodeToString(strategy, body)
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