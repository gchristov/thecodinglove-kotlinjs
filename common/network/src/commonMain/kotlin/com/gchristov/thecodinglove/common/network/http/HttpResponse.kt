package com.gchristov.thecodinglove.common.network.http

import arrow.core.Either
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import io.ktor.http.*
import kotlinx.serialization.encodeToString

interface HttpResponse {
    suspend fun send(string: String): Either<Throwable, Unit>

    suspend fun sendFile(localPath: String): Either<Throwable, Unit>

    suspend fun setHeader(
        header: String,
        value: String,
    ): Either<Throwable, Unit>

    suspend fun redirect(path: String): Either<Throwable, Unit>

    suspend fun status(status: Int): Either<Throwable, Unit>
}

suspend inline fun <reified T> HttpResponse.sendJson(
    status: Int = 200,
    data: T,
    jsonSerializer: JsonSerializer,
): Either<Throwable, Unit> = either {
    val content = Either.catch { jsonSerializer.json.encodeToString(data) }
        .mapLeft { Throwable("Error encoding HTTP response JSON${it.message?.let { m -> ": $m" } ?: ""}", it) }
        .bind()
    send(status = status, content = content, contentType = ContentType.Application.Json).bind()
}

suspend fun HttpResponse.sendText(
    status: Int = 200,
    text: String,
): Either<Throwable, Unit> = send(status = status, content = text, contentType = ContentType.Text.Plain)

suspend fun HttpResponse.sendEmpty(
    status: Int = 200,
    contentType: ContentType = ContentType.Application.Json,
): Either<Throwable, Unit> = send(status = status, content = "", contentType = contentType)

suspend fun HttpResponse.send(
    status: Int,
    content: String,
    contentType: ContentType,
): Either<Throwable, Unit> = either {
    status(status).bind()
    setHeader(header = "Content-Type", value = contentType.toString()).bind()
    send(content).bind()
}
