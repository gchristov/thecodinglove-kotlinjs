package com.gchristov.thecodinglove.commonservicedata.http

import arrow.core.Either
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import io.ktor.http.*
import kotlinx.serialization.encodeToString

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
    jsonSerializer: JsonSerializer,
): Either<Throwable, Unit> = try {
    send(
        status = status,
        content = jsonSerializer.json.encodeToString(data),
        contentType = ContentType.Application.Json
    )
    Either.Right(Unit)
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "Error encoding HTTP response JSON${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}

fun HttpResponse.sendText(
    status: Int = 200,
    text: String,
) = send(
    status = status,
    content = text,
    contentType = ContentType.Text.Plain,
)

fun HttpResponse.sendEmpty(
    status: Int = 200,
    contentType: ContentType = ContentType.Application.Json,
) = send(
    status = status,
    content = "",
    contentType = contentType
)

fun HttpResponse.send(
    status: Int,
    content: String,
    contentType: ContentType,
): Either<Throwable, Unit> = try {
    status(status)
    setHeader(
        header = "Content-Type",
        value = contentType.toString()
    )
    send(content)
    Either.Right(Unit)
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "Error sending HTTP response${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}