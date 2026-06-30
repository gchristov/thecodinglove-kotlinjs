package com.gchristov.thecodinglove.common.network.http

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.error
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface HttpHandler : Handler {
    val dispatcher: CoroutineDispatcher
    val log: Logger
    val jsonSerializer: JsonSerializer

    fun httpConfig(): HttpConfig

    suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit>

    override suspend fun initialise(): Either<Throwable, Unit> = Either.Right(Unit)

    suspend fun handleError(
        error: Throwable,
        response: HttpResponse,
    ): Either<Throwable, Unit> = response.sendJson(
        status = 400,
        data = error.toHttpError(),
        jsonSerializer = jsonSerializer,
    )

    fun handleHttpRequest(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        val tag = this::class.simpleName
        CoroutineScope(dispatcher).launch {
            request.bodyString.takeIf { it?.isNotBlank() == true }
                ?.let { log.debug(tag, "Received request: bodyString=$it") }
                ?: log.debug(tag, "Received request")
            try {
                handleHttpRequestAsync(request, response).getOrElse { handlerError ->
                    log.error(tag, handlerError) { "Error handling request" }
                    handleError(handlerError, response).getOrElse { errorHandlerError ->
                        log.error(tag, errorHandlerError) { "Error sending error response" }
                        return@launch
                    }
                    log.debug(tag, "Request error sent successfully")
                    return@launch
                }
                log.debug(tag, "Request handled successfully")
            } catch (uncaughtHandlerError: Throwable) {
                log.error(tag, uncaughtHandlerError) { "Uncaught handler error" }
                try {
                    handleError(uncaughtHandlerError, response).getOrElse { errorHandlerError ->
                        log.error(tag, errorHandlerError) { "Error sending uncaught handler error" }
                        return@launch
                    }
                    log.debug(tag, "Request uncaught handler error sent successfully")
                } catch (lastResort: Throwable) {
                    log.error(tag, lastResort) { "Uncaught last resort error" }
                }
            }
        }
    }

    data class HttpConfig(
        val method: HttpMethod,
        val path: String,
        val contentType: ContentType,
    )
}

@Serializable
private data class HttpError(val errorMessage: String)

private fun Throwable.toHttpError() = HttpError(
    errorMessage = message ?: "Something unexpected happened. Please try again."
)
