package com.gchristov.thecodinglove.commonservicedata.api

object ApiServiceRegistrations {
    private val requestFacade = ApiRequestFacade()

    fun register(
        callback: (
            request: ApiRequest,
            response: FirebaseFunctionsHttpsResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        callback(requestFacade(request), response)
    }
}

private class ApiRequestFacade {
    private val parametersMapFacade = ApiParametersMapFacade()

    operator fun invoke(request: FirebaseFunctionsHttpsRequest): ApiRequest = object : ApiRequest {
        override val headers: ApiParameterMap = parametersMapFacade(request.headers)

        override val query: ApiParameterMap = parametersMapFacade(request.query)

        override val body: Any = request.body as Any

        override val rawBody: String = request.rawBody
    }
}

private class ApiParametersMapFacade {
    operator fun invoke(
        parameterMap: FirebaseFunctionsHttpsParameterMap
    ): ApiParameterMap = object : ApiParameterMap {
        override fun <T> get(key: String): T? = parameterMap[key]
    }
}