package com.gchristov.thecodinglove.commonservicedata.http

import arrow.core.Either

interface HttpService {
    suspend fun initialise(
        handlers: List<HttpHandler>,
        staticWebsiteRoot: String? = null,
        port: Int,
    ): Either<Throwable, Unit>

    suspend fun start(): Either<Throwable, Unit>
}