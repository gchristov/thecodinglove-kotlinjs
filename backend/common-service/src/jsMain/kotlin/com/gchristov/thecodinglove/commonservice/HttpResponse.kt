package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import co.touchlab.kermit.Logger
import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface HttpResponse {
    fun send(string: String)

    fun sendFile(localPath: String)

    fun setHeader(
        header: String,
        value: String,
    )

    fun redirect(path: String)

    fun status(status: Int)
}

inline fun <reified T> HttpResponse.sendJson(
    status: Int = 200,
    data: T,
    jsonSerializer: Json,
    log: Logger,
): Either<Throwable, Unit> = try {
    send(
        status = status,
        content = jsonSerializer.encodeToString(data),
        contentType = ContentType.Application.Json,
        log = log
    )
    Either.Right(Unit)
} catch (error: Throwable) {
    log.e(error) { error.message ?: "Error during API response send" }
    Either.Left(error)
}

fun HttpResponse.sendText(
    status: Int = 200,
    text: String,
    log: Logger,
) = send(
    status = status,
    content = text,
    contentType = ContentType.Text.Plain,
    log = log,
)

fun HttpResponse.sendEmpty(
    status: Int = 200,
    contentType: ContentType = ContentType.Application.Json,
    log: Logger,
) = send(
    status = status,
    content = "",
    contentType = contentType,
    log = log
)

fun HttpResponse.send(
    status: Int,
    content: String,
    contentType: ContentType,
    log: Logger,
): Either<Throwable, Unit> = try {
    status(status)
    setHeader(
        header = "Content-Type",
        value = contentType.toString()
    )
    send(content)
    Either.Right(Unit)
} catch (error: Throwable) {
    log.e(error) { error.message ?: "Error during response send" }
    Either.Left(error)
}