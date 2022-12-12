package com.gchristov.thecodinglove.commonservice

import kotlinx.serialization.json.Json

abstract class Service(jsonParser: Json) {
    abstract fun register()

    protected fun registerApiCallback(
        callback: (
            request: ApiRequest,
            response: ApiResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        callback(request, response)
    }
}