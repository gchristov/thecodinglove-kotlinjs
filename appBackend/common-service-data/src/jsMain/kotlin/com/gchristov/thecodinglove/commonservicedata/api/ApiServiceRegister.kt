package com.gchristov.thecodinglove.commonservicedata.api

import com.gchristov.thecodinglove.commonservicedata.FirebaseFunctions

interface ApiServiceRegister {
    fun register(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    )
}

internal class RealApiServiceRegister : ApiServiceRegister {
    override fun register(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        callback(request.toApiRequest(), response.toApiResponse())
    }
}