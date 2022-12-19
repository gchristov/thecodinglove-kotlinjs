package com.gchristov.thecodinglove.commonservicedata.pubsub

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