package com.gchristov.thecodinglove

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

external class FirebaseFunctionsResponse {
    fun send(data: String)
}