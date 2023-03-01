package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import co.touchlab.kermit.Logger
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
    private val jsonSerializer: Json,
    private val log: Logger,
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
            request.rawBody?.let { log.d("Received API request with body: rawBody=$it") }
            handleRequest(
                request = request,
                response = response
            ).fold(
                ifLeft = { handleError ->
                    log.e(handleError) { handleError.message ?: "Error handling request" }
                    sendError(
                        error = handleError,
                        response = response
                    ).fold(
                        ifLeft = { sendError ->
                            log.e(sendError) { sendError.message ?: "Error sending error response" }
                        },
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