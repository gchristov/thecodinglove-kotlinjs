package com.gchristov.thecodinglove.commonservice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class ApiService : CoroutineScope {

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
                    request = request,
                    response = response
                )
            }
        }
}