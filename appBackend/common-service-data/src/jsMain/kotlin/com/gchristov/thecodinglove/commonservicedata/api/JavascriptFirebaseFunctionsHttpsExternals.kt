package com.gchristov.thecodinglove.commonservicedata.api

external object FirebaseFunctionsHttps {
    fun onRequest(
        callback: (
            request: FirebaseFunctionsHttpsRequest,
            response: FirebaseFunctionsHttpsResponse
        ) -> Unit
    )
}

external class FirebaseFunctionsHttpsRequest {
    val headers: FirebaseFunctionsHttpsParameterMap
    val query: FirebaseFunctionsHttpsParameterMap
    val body: dynamic
    val rawBody: String
}

external class FirebaseFunctionsHttpsParameterMap

inline operator fun <T> FirebaseFunctionsHttpsParameterMap.get(key: String): T? {
    return asDynamic()[key] as? T
}

external class FirebaseFunctionsHttpsResponse {
    fun setHeader(
        header: String,
        value: String
    )

    fun send(data: String)

    fun status(status: Int): FirebaseFunctionsHttpsResponse
}