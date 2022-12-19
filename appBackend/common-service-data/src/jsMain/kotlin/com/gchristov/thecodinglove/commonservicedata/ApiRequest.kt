package com.gchristov.thecodinglove.commonservicedata

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ApiRequestFacade(private val parametersMapFacade: ParametersMapFacade) {
    fun transform(realApiRequest: RealApiRequest): ApiRequest = object : ApiRequest {
        override val headers: ParametersMap = parametersMapFacade.transform(realApiRequest.headers)

        override val query: ParametersMap = parametersMapFacade.transform(realApiRequest.query)

        override val body: Any = realApiRequest.body as Any

        override val rawBody: String = realApiRequest.rawBody
    }
}

class ParametersMapFacade {
    fun transform(realParametersMap: RealParametersMap): ParametersMap = object : ParametersMap {
        override fun <T> get(key: String): T? = realParametersMap[key]
    }
}

interface ApiRequest {
    val headers: ParametersMap
    val query: ParametersMap
    val body: Any
    val rawBody: String
}

inline fun <reified T> ApiRequest.bodyAsJson(
    jsonSerializer: Json
): T = jsonSerializer.decodeFromString(string = JSON.stringify(body))

interface ParametersMap {
    operator fun <T> get(key: String): T?
}