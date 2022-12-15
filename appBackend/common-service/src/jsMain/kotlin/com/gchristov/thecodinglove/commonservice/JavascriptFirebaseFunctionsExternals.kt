package com.gchristov.thecodinglove.commonservice

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.js.Promise

external var exports: dynamic

@JsModule("firebase-functions")
@JsNonModule
internal external object FirebaseFunctions {
    val https: FirebaseFunctionsHttps
    val pubsub: FirebaseFunctionsPubSub
}

// HTTPS

internal external object FirebaseFunctionsHttps {
    fun onRequest(
        callback: (
            request: ApiRequest,
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

external class ApiRequest {
    val headers: ParametersMap
    val query: ParametersMap
}

fun ApiRequest.bodyAsString(): String {
    val rawBody = asDynamic().rawBody
    // Kotlin .toString() doesn't really work well here and we end up with wrong content
    return js("rawBody.toString()").toString()
}

inline fun <reified T> ApiRequest.bodyAsJson(
    jsonSerializer: Json
): T = jsonSerializer.decodeFromString(string = JSON.stringify(asDynamic().body))

external class ParametersMap

inline operator fun <T> ParametersMap.get(key: String): T? = asDynamic()[key] as? T

// PubSub

internal external object FirebaseFunctionsPubSub {
    fun topic(name: String): PubSubSubscriberTopic
}

internal external object PubSubSubscriberTopic {
    fun onPublish(callback: (message: PubSubMessage) -> Promise<Unit>)
}

external class PubSubMessage

inline fun <reified T> PubSubMessage.bodyAsJson(
    jsonSerializer: Json
): T = jsonSerializer.decodeFromString(string = JSON.stringify(asDynamic().json))