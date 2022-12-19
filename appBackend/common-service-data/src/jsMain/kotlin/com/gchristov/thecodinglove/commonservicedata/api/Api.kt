package com.gchristov.thecodinglove.commonservicedata.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface ApiRequest {
    val headers: ApiParameterMap
    val query: ApiParameterMap
    val body: Any?
    val rawBody: String?
}

inline fun <reified T> ApiRequest.bodyAsJson(
    jsonSerializer: Json
): T? = body?.let { jsonSerializer.decodeFromString<T>(string = JSON.stringify(it)) }

interface ApiParameterMap {
    operator fun <T> get(key: String): T?
}

interface ApiResponse {
    fun setHeader(
        header: String,
        value: String
    )

    fun send(data: String)

    fun status(status: Int)
}

fun ApiResponse.sendJson(
    status: Int = 200,
    data: String,
) {
    status(status)
    setHeader(
        header = "Content-Type",
        value = "application/json"
    )
    send(data)
}

internal fun FirebaseFunctionsHttpsRequest.toApiRequest() = object : ApiRequest {
    override val headers: ApiParameterMap = this@toApiRequest.headers.toApiParametersMap()
    override val query: ApiParameterMap = this@toApiRequest.query.toApiParametersMap()
    override val body: Any? = this@toApiRequest.body
    override val rawBody: String? = this@toApiRequest.rawBody
}

internal fun FirebaseFunctionsHttpsResponse.toApiResponse() = object : ApiResponse {
    override fun setHeader(
        header: String,
        value: String
    ) = this@toApiResponse.setHeader(
        header = header,
        value = value
    )

    override fun send(data: String) = this@toApiResponse.send(data)

    override fun status(status: Int) {
        this@toApiResponse.status(status)
    }
}

private fun FirebaseFunctionsHttpsParameterMap.toApiParametersMap() = object : ApiParameterMap {
    override fun <T> get(key: String): T? = this@toApiParametersMap[key]
}