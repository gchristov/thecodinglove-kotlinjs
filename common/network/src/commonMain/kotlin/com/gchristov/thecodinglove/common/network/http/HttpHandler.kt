package com.gchristov.thecodinglove.common.network.http

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