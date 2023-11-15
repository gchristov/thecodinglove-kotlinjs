package com.gchristov.thecodinglove.commonservice.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.commonkotlin.Buffer
import com.gchristov.thecodinglove.commonkotlin.process
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import kotlinx.coroutines.await
import kotlinx.serialization.SerializationStrategy

internal class GoogleCloudPubSubPublisher(
    private val pubSub: GoogleCloudPubSubExternals.PubSub
) : PubSubPublisher {

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-gcp.json"
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