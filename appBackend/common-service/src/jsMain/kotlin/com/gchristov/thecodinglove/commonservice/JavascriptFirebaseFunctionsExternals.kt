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

internal external object FirebaseFunctionsHttps {
    fun onRequest(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    )
}

internal external object FirebaseFunctionsPubSub {
    fun topic(name: String): PubSubTopic
}

internal external object PubSubTopic {
    fun onPublish(callback: (message: PubSubMessage) -> Promise<Unit>)
}

external class PubSubMessage {
    val json: ParametersMap
}

external class ApiResponse {
    fun send(data: String)

    fun status(status: Int): ApiResponse
}

external class ApiRequest {
    val query: ParametersMap
    val body: ParametersMap
}

external class ParametersMap

inline operator fun <T> ParametersMap.get(key: String): T? = asDynamic()[key] as? T

inline fun <reified T> ParametersMap.bodyFromJson(jsonParser: Json): T =
    jsonParser.decodeFromString(string = JSON.stringify(this))