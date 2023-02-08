package com.gchristov.thecodinglove.commonservicedata.pubsub

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface PubSubMessage {
    val json: String?
}

inline fun <reified T> PubSubMessage.bodyAsJson(
    jsonSerializer: Json
): T? = json?.let { jsonSerializer.decodeFromString(it) }

internal fun FirebaseFunctionsPubSubMessage.toPubSubMessage() = object : PubSubMessage {
    override val json: String? = if (this@toPubSubMessage.json != null) {
        JSON.stringify(this@toPubSubMessage.json)
    } else {
        null
    }
}