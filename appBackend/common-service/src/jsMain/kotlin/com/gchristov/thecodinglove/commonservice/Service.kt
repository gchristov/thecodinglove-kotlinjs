package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommondi.DiGraph
import com.gchristov.thecodinglove.kmpcommondi.inject
import kotlinx.serialization.json.Json

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

    inline fun <reified T> registerApiCallback(
        crossinline callback: (
            request: Either<Exception, T>,
            response: ApiResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        try {
            val jsonParser = DiGraph.inject<Json>()
            val command: T = request.body.bodyFromJson(jsonParser)
            callback(Either.Right(command), response)
        } catch (error: Exception) {
            callback(Either.Left(error), response)
        }
    }
}