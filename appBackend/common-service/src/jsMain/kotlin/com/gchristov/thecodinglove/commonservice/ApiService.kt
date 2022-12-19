package com.gchristov.thecodinglove.commonservice

import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegistrations
import com.gchristov.thecodinglove.commonservicedata.api.sendJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
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
        ApiServiceRegistrations.register { request, response ->
            launch {
                handleRequest(
                    request = request,
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
            data = error.toError(),
            jsonSerializer = jsonSerializer
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