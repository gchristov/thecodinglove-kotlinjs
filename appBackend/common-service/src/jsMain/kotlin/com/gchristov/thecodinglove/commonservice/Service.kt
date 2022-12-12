package com.gchristov.thecodinglove.commonservice

abstract class Service {
    abstract fun register()

    fun registerApiCallback(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        callback(request, response)
    }
}