package com.gchristov.thecodinglove.commonservicedata.api

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