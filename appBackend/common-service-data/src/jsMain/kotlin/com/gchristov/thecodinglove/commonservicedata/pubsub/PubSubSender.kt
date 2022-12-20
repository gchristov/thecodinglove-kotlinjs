package com.gchristov.thecodinglove.commonservicedata.pubsub

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
) {
    sendMessage(topic = topic, body = jsonSerializer.encodeToString(body))
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
        GoogleCloudPubSub(projectId).topic(topic).publish(Buffer.from(body))
    }
}