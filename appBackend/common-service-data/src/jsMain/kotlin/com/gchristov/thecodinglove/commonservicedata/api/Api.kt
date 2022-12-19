package com.gchristov.thecodinglove.commonservicedata.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// Request

interface ApiRequest {
    val headers: ApiParameterMap
    val query: ApiParameterMap
    val body: Any
    val rawBody: String
}

inline fun <reified T> ApiRequest.bodyAsJson(
    jsonSerializer: Json
): T = jsonSerializer.decodeFromString(string = JSON.stringify(body))

interface ApiParameterMap {
    operator fun <T> get(key: String): T?
}

// Response

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