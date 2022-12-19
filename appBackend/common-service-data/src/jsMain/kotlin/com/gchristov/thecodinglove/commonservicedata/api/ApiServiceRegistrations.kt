package com.gchristov.thecodinglove.commonservicedata.api

import com.gchristov.thecodinglove.commonservicedata.FirebaseFunctions

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