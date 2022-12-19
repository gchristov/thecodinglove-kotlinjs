package com.gchristov.thecodinglove.commonservicedata.api

object ApiServiceRegistrations {
    fun register(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        callback(request.toApiRequest(), response.toApiResponse())
    }
}

private fun FirebaseFunctionsHttpsRequest.toApiRequest() = object : ApiRequest {
    override val headers: ApiParameterMap = this@toApiRequest.headers.toApiParametersMap()
    override val query: ApiParameterMap = this@toApiRequest.query.toApiParametersMap()
    override val body: Any = this@toApiRequest.body as Any
    override val rawBody: String = this@toApiRequest.rawBody
}

private fun FirebaseFunctionsHttpsResponse.toApiResponse() = object : ApiResponse {
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