package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import kotlinx.coroutines.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface PubSubSender {
    suspend fun sendMessage(
        topic: String,
        body: String
    )
}

suspend inline fun <reified T> PubSubSender.sendMessage(
    topic: String,
    body: T,
    jsonSerializer: Json
): Either<Throwable, Unit> = try {
    Either.Right(sendMessage(topic = topic, body = jsonSerializer.encodeToString(body)))
} catch (error: Throwable) {
    Either.Left(error)
}

internal class RealPubSubSender(private val projectId: String) : PubSubSender {
    override suspend fun sendMessage(
        topic: String,
        body: String
    ) {
        println("Sending PubSub message: topic=$topic, body=$body")
        val messageId = GoogleCloudPubSub(projectId)
            .topic(topic)
            .publish(Buffer.from(body))
            .await()
        println("PubSub message sent: id=$messageId")
    }
}