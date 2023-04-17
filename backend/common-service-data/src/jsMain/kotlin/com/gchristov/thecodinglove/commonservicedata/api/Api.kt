package com.gchristov.thecodinglove.commonservicedata.api

import arrow.core.Either
import co.touchlab.kermit.Logger
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface ApiRequest {
    val headers: ApiParameterMap
    val query: ApiParameterMap
    val body: Any?
    val rawBody: String?
}

// Body is decoded as per the following Firebase specs
// https://firebase.google.com/docs/functions/http-events#read_values_from_the_request
inline fun <reified T> ApiRequest.decodeBodyFromJson(
    jsonSerializer: Json,
    log: Logger
): Either<Throwable, T?> = try {
    Either.Right(body?.let { jsonSerializer.decodeFromString<T>(string = JSON.stringify(it)) })
} catch (error: Throwable) {
    log.e(error) { error.message ?: "Error during API request body decode" }
    Either.Left(error)
}

interface ApiParameterMap {
    operator fun <T> get(key: String): T?
}

interface ApiResponse {
    fun setHeader(
        header: String,
        value: String
    )

    fun send(data: String)

    fun status(status: Int)

    fun redirect(path: String)
}

inline fun <reified T> ApiResponse.sendJson(
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

fun ApiResponse.sendText(
    status: Int = 200,
    text: String,
    log: Logger,
) = send(
    status = status,
    content = text,
    contentType = ContentType.Text.Plain,
    log = log,
)

fun ApiResponse.sendEmpty(
    status: Int = 200,
    contentType: ContentType = ContentType.Application.Json,
    log: Logger,
) = send(
    status = status,
    content = "",
    contentType = contentType,
    log = log
)

fun ApiResponse.send(
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
    log.e(error) { error.message ?: "Error during API response send" }
    Either.Left(error)
}

internal fun FirebaseFunctionsHttpsRequest.toApiRequest() = object : ApiRequest {
    override val headers: ApiParameterMap = this@toApiRequest.headers.toApiParametersMap()
    override val query: ApiParameterMap = this@toApiRequest.query.toApiParametersMap()
    override val body: Any? = this@toApiRequest.body
    override val rawBody: String? = this@toApiRequest.rawBody
}

internal fun FirebaseFunctionsHttpsResponse.toApiResponse() = object : ApiResponse {
    override fun setHeader(
        header: String,
        value: String
    ) = this@toApiResponse.setHeader(
        header = header,
        value = value
    )

    override fun send(data: String) = this@toApiResponse.send(data)

    override fun status(status: Int) {
        this@toApiResponse.status(status)
    }

    override fun redirect(path: String) = this@toApiResponse.redirect(path)
}

private fun FirebaseFunctionsHttpsParameterMap.toApiParametersMap() = object : ApiParameterMap {
    override fun <T> get(key: String): T? = this@toApiParametersMap[key]
}