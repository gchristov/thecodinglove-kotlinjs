package com.gchristov.thecodinglove.commonservice

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

external var exports: dynamic

@JsModule("firebase-functions")
@JsNonModule
external object FirebaseFunctions {
    val https: FirebaseFunctionsHttps
}

external object FirebaseFunctionsHttps {
    fun onRequest(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    )
}

external class ApiResponse {
    fun send(data: String)
}

external class ApiRequest {
    val query: ParametersMap
    val body: ParametersMap
}

external class ParametersMap

inline operator fun <T> ParametersMap.get(key: String): T? = asDynamic()[key] as? T

inline fun <reified T> ParametersMap.bodyFromJson(jsonParser: Json): T =
    jsonParser.decodeFromString(string = JSON.stringify(this))