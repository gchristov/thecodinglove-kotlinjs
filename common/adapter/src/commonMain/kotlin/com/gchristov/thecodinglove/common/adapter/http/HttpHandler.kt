package com.gchristov.thecodinglove.common.adapter.http

import com.gchristov.thecodinglove.common.adapter.Handler
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