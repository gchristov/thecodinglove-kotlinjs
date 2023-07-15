package com.gchristov.thecodinglove.commonservice

import io.ktor.http.*

interface HttpBackendService {
    fun start(port: Int)

    fun serveStaticContent(localPath: String)

    fun registerHandler(
        method: HttpMethod,
        path: String,
        contentType: ContentType = ContentType.Any,
        handler: HttpHandler,
    )
}