package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.sendJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

abstract class ApiService(
    private val apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json
) : CoroutineScope {

    private val job = Job()

    abstract fun register()

    abstract suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ): Either<Throwable, Unit>

    override val coroutineContext: CoroutineContext
        get() = job

    protected fun registerForApiCallbacks() = apiServiceRegister.register { request, response ->
        launch {
            handleRequest(
                request = request,
                response = response
            ).fold(
                ifLeft = {
                    sendError(
                        error = it,
                        response = response
                    ).fold(
                        ifLeft = {},
                        ifRight = {
                            // TODO: Add some request metrics in here
                        }
                    )
                },
                ifRight = {
                    // TODO: Add some request metrics in here
                }
            )
        }
    }

    private fun sendError(
        error: Throwable,
        response: ApiResponse
    ) = response.sendJson(
        status = 400,
        data = error.toError(),
        jsonSerializer = jsonSerializer
    )
}

@Serializable
private data class Error(
    val errorMessage: String
)

private fun Throwable.toError() = Error(
    errorMessage = message ?: "Something unexpected happened. Please try again."
)