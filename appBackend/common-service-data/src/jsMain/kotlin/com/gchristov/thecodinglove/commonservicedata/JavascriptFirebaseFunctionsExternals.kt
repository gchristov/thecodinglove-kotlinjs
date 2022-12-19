package com.gchristov.thecodinglove.commonservicedata

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.Promise

external var exports: dynamic

@JsModule("firebase-functions")
@JsNonModule
external object FirebaseFunctions {
    val https: FirebaseFunctionsHttps
    val pubsub: FirebaseFunctionsPubSub
}

// HTTPS

external object FirebaseFunctionsHttps {
    fun onRequest(
        callback: (
            request: RealApiRequest,
            response: ApiResponse
        ) -> Unit
    )
}

external class ApiResponse {
    fun setHeader(
        header: String,
        value: String
    )

    fun send(data: String)

    fun status(status: Int): ApiResponse
}

fun ApiResponse.sendJson(
    status: Int = 200,
    data: String,
) {
    this.setHeader(
        header = "Content-Type",
        value = "application/json"
    )
    this.status(status).send(data)
}

external class RealApiRequest {
    val headers: RealParametersMap
    val query: RealParametersMap
    val body: dynamic
    val rawBody: String
}

external class RealParametersMap

inline operator fun <T> RealParametersMap.get(key: String): T? = asDynamic()[key] as? T

// PubSub

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