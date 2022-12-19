package com.gchristov.thecodinglove.commonservicedata.pubsub

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.Promise

external object FirebaseFunctionsPubSub {
    fun topic(name: String): PubSubSubscriberTopic
}

external object PubSubSubscriberTopic {
    fun onPublish(callback: (message: PubSubMessage) -> Promise<Unit>)
}

external class PubSubMessage

inline fun <reified T> PubSubMessage.bodyAsJson(
    jsonSerializer: Json
): T = jsonSerializer.decodeFromString(string = JSON.stringify(asDynamic().json))