package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.sendJson
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

abstract class BaseHttpHandler(
    private val dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
) : HttpHandler, CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    protected abstract suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit>

    override suspend fun initialise(): Either<Throwable, Unit> = Either.Right(Unit)

    open fun handleError(
        error: Throwable,
        response: HttpResponse
    ): Either<Throwable, Unit> = response.sendJson(
        status = 400,
        data = error.toHttpError(),
        jsonSerializer = jsonSerializer
    )

    override fun handleHttpRequest(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        launch(dispatcher) {
            request.bodyString?.let { log.d("Received HTTP request: bodyString=$it") } ?: log.d("Received request")
            try {
                handleHttpRequestAsync(
                    request = request,
                    response = response,
                ).fold(
                    ifLeft = { handlerError ->
                        log.e(handlerError) { "Error handling HTTP request" }
                        handleError(
                            error = handlerError,
                            response = response,
                        ).fold(
                            ifLeft = { errorHandlerError ->
                                log.e(errorHandlerError) { "Error sending HTTP error response" }
                            },
                            ifRight = {
                                // TODO: Add some request metrics in here
                                log.d("HTTP request error sent successfully")
                            }
                        )
                    },
                    ifRight = {
                        // TODO: Add some request metrics in here
                        log.d("HTTP request handled successfully")
                    }
                )
            } catch (uncaughtHandlerError: Throwable) {
                log.e(uncaughtHandlerError) { "Uncaught HTTP handler error" }
                try {
                    handleError(
                        error = uncaughtHandlerError,
                        response = response,
                    ).fold(
                        ifLeft = { errorHandlerError ->
                            log.e(errorHandlerError) { "Error sending uncaught HTTP handler error" }
                        },
                        ifRight = {
                            // TODO: Add some request metrics in here
                            log.d("Request uncaught handler error sent successfully")
                        }
                    )
                } catch (lastResort: Throwable) {
                    log.e(lastResort) { "Uncaught HTTP last resort error" }
                }
            }
        }
    }
}

@Serializable
private data class HttpError(
    val errorMessage: String
)

private fun Throwable.toHttpError() = HttpError(
    errorMessage = message ?: "Something unexpected happened. Please try again."
)