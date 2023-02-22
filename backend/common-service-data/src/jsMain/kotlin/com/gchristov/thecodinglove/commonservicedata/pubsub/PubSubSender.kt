package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface PubSubSender {
    fun sendMessage(
        topic: String,
        body: String
    )
}

inline fun <reified T> PubSubSender.sendMessage(
    topic: String,
    body: T,
    jsonSerializer: Json
): Either<Throwable, Unit> = try {
    Either.Right(sendMessage(topic = topic, body = jsonSerializer.encodeToString(body)))
} catch (error: Throwable) {
    Either.Left(error)
}

internal class RealPubSubSender(private val projectId: String) : PubSubSender {
    override fun sendMessage(
        topic: String,
        body: String
    ) {
        println(
            "Sending PubSub message\n" +
                    "topic: $topic\n" +
                    "body: $body"
        )
        GoogleCloudPubSub(projectId).topic(
            name = topic,
            options = js(
                """{
                    batching: {
                    maxMessages: 1
                }
                }"""
            )
        ).publish(Buffer.from(body))
    }
}

private val DefaultPubSubOptions = GoogleGloudPubSubPublishOptions().apply {
    batching = GoogleGloudPubSubBatchOptions().apply {
        maxMessages = 1
    }
}