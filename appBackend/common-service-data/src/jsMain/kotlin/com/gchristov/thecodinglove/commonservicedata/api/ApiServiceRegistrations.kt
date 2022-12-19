package com.gchristov.thecodinglove.commonservicedata.api

object ApiServiceRegistrations {
    private val requestFacade = ApiRequestFacade()
    private val responseFacade = ApiResponseFacade()

    fun register(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        callback(requestFacade(request), responseFacade(response))
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

private class ApiResponseFacade {
    operator fun invoke(
        response: FirebaseFunctionsHttpsResponse
    ): ApiResponse = object : ApiResponse {
        override fun setHeader(
            header: String,
            value: String
        ) = response.setHeader(
            header = header,
            value = value
        )

        override fun send(data: String) = response.send(data)

        override fun status(status: Int) {
            response.status(status)
        }
    }
}

private class ApiParametersMapFacade {
    operator fun invoke(
        parameterMap: FirebaseFunctionsHttpsParameterMap
    ): ApiParameterMap = object : ApiParameterMap {
        override fun <T> get(key: String): T? = parameterMap[key]
    }
}