package com.gchristov.thecodinglove.commonservice.http

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.sendEmpty
import com.gchristov.thecodinglove.commonservicedata.http.sendJson
import io.ktor.http.*
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

    fun httpConfig(): Config

    fun handle(
        request: HttpRequest,
        response: HttpResponse,
    )

    data class Config(
        val method: HttpMethod,
        val path: String,
        val contentType: ContentType,
    )
}

abstract class BaseHttpHandler(
    private val jsonSerializer: Json,
    private val log: Logger,
) : HttpHandler, CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    override suspend fun initialise(): Either<Throwable, Unit> = Either.Right(Unit)

    open suspend fun handleAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = response.sendEmpty()

    open fun handleError(
        error: Throwable,
        response: HttpResponse
    ): Either<Throwable, Unit> = response.sendJson(
        status = 400,
        data = error.toHttpError(),
        jsonSerializer = jsonSerializer
    )

    override fun handle(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        launch {
            request.bodyString?.let { log.d("Received request: bodyString=$it") } ?: log.d("Received request")
            try {
                handleAsync(
                    request = request,
                    response = response,
                ).fold(
                    ifLeft = { handlerError ->
                        log.e(handlerError) { "Error handling async request${handlerError.message?.let { ": $it" } ?: ""}" }
                        handleError(
                            error = handlerError,
                            response = response,
                        ).fold(
                            ifLeft = { errorHandlerError ->
                                log.e(errorHandlerError) { "Error sending error response${errorHandlerError.message?.let { ": $it" } ?: ""}" }
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
            } catch (uncaughtHandlerError: Throwable) {
                log.e(uncaughtHandlerError) { "Uncaught handler error${uncaughtHandlerError.message?.let { ": $it" } ?: ""}" }
                try {
                    handleError(
                        error = uncaughtHandlerError,
                        response = response,
                    ).fold(
                        ifLeft = { errorHandlerError ->
                            log.e(errorHandlerError) { "Error sending uncaught handler error${errorHandlerError.message?.let { ": $it" } ?: ""}" }
                        },
                        ifRight = {
                            // TODO: Add some request metrics in here
                        }
                    )
                } catch (lastResort: Throwable) {
                    log.e(lastResort) { "Uncaught last resort error${lastResort.message?.let { ": $it" } ?: ""}" }
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