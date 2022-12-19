package com.gchristov.thecodinglove.commonservicedata.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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