package com.gchristov.thecodinglove.common.network.http

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.error
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

    private val tag = this::class.simpleName
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
            request.bodyString.takeIf { it?.isNotBlank() == true }
                ?.let { log.debug(tag, "Received request: bodyString=$it") }
                ?: log.debug(tag, "Received request")
            try {
                handleHttpRequestAsync(
                    request = request,
                    response = response,
                ).fold(
                    ifLeft = { handlerError ->
                        log.error(tag, handlerError) { "Error handling request" }
                        handleError(
                            error = handlerError,
                            response = response,
                        ).fold(
                            ifLeft = { errorHandlerError ->
                                log.error(tag, errorHandlerError) { "Error sending error response" }
                            },
                            ifRight = {
                                // TODO: Add some request metrics in here
                                log.debug(tag, "Request error sent successfully")
                            }
                        )
                    },
                    ifRight = {
                        // TODO: Add some request metrics in here
                        log.debug(tag, "Request handled successfully")
                    }
                )
            } catch (uncaughtHandlerError: Throwable) {
                log.error(tag, uncaughtHandlerError) { "Uncaught handler error" }
                try {
                    handleError(
                        error = uncaughtHandlerError,
                        response = response,
                    ).fold(
                        ifLeft = { errorHandlerError ->
                            log.error(tag, errorHandlerError) { "Error sending uncaught handler error" }
                        },
                        ifRight = {
                            // TODO: Add some request metrics in here
                            log.debug(tag, "Request uncaught handler error sent successfully")
                        }
                    )
                } catch (lastResort: Throwable) {
                    log.error(tag, lastResort) { "Uncaught last resort error" }
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