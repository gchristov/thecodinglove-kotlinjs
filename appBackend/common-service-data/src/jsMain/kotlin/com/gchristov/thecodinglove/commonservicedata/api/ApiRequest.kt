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

class ApiRequestFacade(private val parametersMapFacade: ApiParametersMapFacade) {
    operator fun invoke(request: FirebaseFunctionsHttpsRequest): ApiRequest = object : ApiRequest {
        override val headers: ApiParameterMap = parametersMapFacade(request.headers)

        override val query: ApiParameterMap = parametersMapFacade(request.query)

        override val body: Any = request.body as Any

        override val rawBody: String = request.rawBody
    }
}

class ApiParametersMapFacade {
    operator fun invoke(
        parameterMap: FirebaseFunctionsHttpsParameterMap
    ): ApiParameterMap = object : ApiParameterMap {
        override fun <T> get(key: String): T? = parameterMap[key]
    }
}