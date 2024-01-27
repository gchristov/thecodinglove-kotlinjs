package com.gchristov.thecodinglove.commonservicedata.http

import com.gchristov.thecodinglove.commonservicedata.Handler
import io.ktor.http.*

interface HttpHandler : Handler {

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