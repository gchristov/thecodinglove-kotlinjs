package com.gchristov.thecodinglove.common.network.http

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class StaticFileHttpHandler(private val path: String) : HttpHandler {
    // File serving is synchronous — handleHttpRequest is fully overridden and the coroutine
    // infrastructure (dispatcher, log, jsonSerializer, handleHttpRequestAsync) is never used.
    override val dispatcher: CoroutineDispatcher get() = error("not used")
    override val log: Logger get() = error("not used")
    override val jsonSerializer: JsonSerializer get() = error("not used")

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "*",
        contentType = ContentType.Any,
    )

    override fun handleHttpRequest(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        response.sendFile(localPath = path)
    }

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = error("not used")
}
