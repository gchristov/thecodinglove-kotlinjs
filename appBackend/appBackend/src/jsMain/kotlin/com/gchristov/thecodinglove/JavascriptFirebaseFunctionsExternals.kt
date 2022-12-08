package com.gchristov.thecodinglove

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@JsModule("firebase-functions")
@JsNonModule
external object FirebaseFunctions {
    val https: FirebaseFunctionsHttps
}

external object FirebaseFunctionsHttps {
    fun onRequest(
        callback: (
            request: FirebaseFunctionsRequest,
            response: FirebaseFunctionsResponse
        ) -> Unit
    )
}

external class FirebaseFunctionsRequest {
    val query: FirebaseFunctionsMap
    val body: FirebaseFunctionsMap
}

external class FirebaseFunctionsMap

inline operator fun <T> FirebaseFunctionsMap.get(key: String): T? = asDynamic()[key] as? T

inline fun <reified T> FirebaseFunctionsMap.parseJson(jsonParser: Json): T? =
    jsonParser.decodeFromString(string = JSON.stringify(this))

external class FirebaseFunctionsResponse {
    fun send(data: String)
}