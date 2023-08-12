package com.gchristov.thecodinglove.commonservice.http

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import io.ktor.http.*

class StaticFileHttpHandler(private val path: String) : HttpHandler {
    override suspend fun initialise(): Either<Throwable, Unit> = Either.Right(Unit)

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
}