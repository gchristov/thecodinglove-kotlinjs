package com.gchristov.thecodinglove.commonservicedata.http

import arrow.core.Either
import co.touchlab.kermit.Logger
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

interface HttpService {
    suspend fun initialise(
        handlers: List<HttpHandler>,
        staticWebsiteRoot: String? = null,
        port: Int,
    ): Either<Throwable, Unit>

    suspend fun start(): Either<Throwable, Unit>
}

interface HttpHandler {
    suspend fun initialise(): Either<Throwable, Unit>

    fun httpConfig(): HttpConfig

    fun handleHttpRequest(
        request: HttpRequest,
        response: HttpResponse,
    )

    data class HttpConfig(
        val method: HttpMethod,
        val path: String,
        val contentType: ContentType,
    )
}

abstract class BaseHttpHandler(
    private val dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
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
            request.bodyString?.let { log.d("Received request: bodyString=$it") } ?: log.d("Received request")
            try {
                handleHttpRequestAsync(
                    request = request,
                    response = response,
                ).fold(
                    ifLeft = { handlerError ->
                        log.e(handlerError) { "Error handling request: trace=${handlerError.stackTraceToString()}" }
                        handleError(
                            error = handlerError,
                            response = response,
                        ).fold(
                            ifLeft = { errorHandlerError ->
                                log.e(errorHandlerError) { "Error sending error response: trace=${errorHandlerError.stackTraceToString()}" }
                            },
                            ifRight = {
                                // TODO: Add some request metrics in here
                                log.d("Request error sent successfully")
                            }
                        )
                    },
                    ifRight = {
                        // TODO: Add some request metrics in here
                        log.d("Request handled successfully")
                    }
                )
            } catch (uncaughtHandlerError: Throwable) {
                log.e(uncaughtHandlerError) { "Uncaught handler error: trace=${uncaughtHandlerError.stackTraceToString()}" }
                try {
                    handleError(
                        error = uncaughtHandlerError,
                        response = response,
                    ).fold(
                        ifLeft = { errorHandlerError ->
                            log.e(errorHandlerError) { "Error sending uncaught handler error: trace=${errorHandlerError.stackTraceToString()}" }
                        },
                        ifRight = {
                            // TODO: Add some request metrics in here
                            log.d("Request uncaught handler error sent successfully")
                        }
                    )
                } catch (lastResort: Throwable) {
                    log.e(lastResort) { "Uncaught last resort error: trace=${lastResort.stackTraceToString()}" }
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