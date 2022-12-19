package com.gchristov.thecodinglove.commonservicedata.api

internal external object FirebaseFunctionsHttps {
    fun onRequest(
        callback: (
            request: FirebaseFunctionsHttpsRequest,
            response: FirebaseFunctionsHttpsResponse
        ) -> Unit
    )
}

internal external class FirebaseFunctionsHttpsRequest {
    val headers: FirebaseFunctionsHttpsParameterMap
    val query: FirebaseFunctionsHttpsParameterMap
    val body: dynamic
    val rawBody: String
}

internal external class FirebaseFunctionsHttpsParameterMap

internal inline operator fun <T> FirebaseFunctionsHttpsParameterMap.get(key: String): T? {
    return asDynamic()[key] as? T
}

internal external class FirebaseFunctionsHttpsResponse {
    fun setHeader(
        header: String,
        value: String
    )

    fun send(data: String)

    fun status(status: Int): FirebaseFunctionsHttpsResponse
}