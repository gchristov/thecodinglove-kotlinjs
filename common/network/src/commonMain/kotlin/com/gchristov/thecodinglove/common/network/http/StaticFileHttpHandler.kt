package com.gchristov.thecodinglove.common.network.http

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StaticFileHttpHandler(private val path: String) : HttpHandler {
    // File serving bypasses the handler coroutine infrastructure — log, jsonSerializer,
    // and handleHttpRequestAsync are never invoked.
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
        CoroutineScope(Dispatchers.Default).launch {
            response.sendFile(localPath = path)
        }
    }

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = error("not used")
}
