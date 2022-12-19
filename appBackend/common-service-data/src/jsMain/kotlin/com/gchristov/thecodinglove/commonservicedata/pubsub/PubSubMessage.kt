package com.gchristov.thecodinglove.commonservicedata.pubsub

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface PubSubMessage {
    val json: Any
}

inline fun <reified T> PubSubMessage.bodyAsJson(
    jsonSerializer: Json
): T = jsonSerializer.decodeFromString(string = JSON.stringify(json))