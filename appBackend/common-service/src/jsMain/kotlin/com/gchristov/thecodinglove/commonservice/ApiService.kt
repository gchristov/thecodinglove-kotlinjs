package com.gchristov.thecodinglove.commonservice

import com.gchristov.thecodinglove.commonservicedata.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

abstract class ApiService(private val jsonSerializer: Json) : CoroutineScope {

    private val job = Job()

    abstract fun register()

    protected abstract suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    )

    override val coroutineContext: CoroutineContext
        get() = job

    protected fun registerForApiCallbacks() =
        FirebaseFunctions.https.onRequest { request, response ->
            launch {
                handleRequest(
                    request = ApiRequestFacade(ParametersMapFacade()).transform(request),
                    response = response
                )
            }
        }

    protected fun sendError(
        error: Throwable,
        response: ApiResponse
    ) {
        error.printStackTrace()
        response.sendJson(
            status = 400,
            data = jsonSerializer.encodeToString(error.toError())
        )
    }
}

@Serializable
private data class Error(
    val errorMessage: String
)

private fun Throwable.toError() = Error(
    errorMessage = message ?: "Something unexpected happened. Please try again."
)