package com.gchristov.thecodinglove.commonservicedata.http

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.ParameterMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface HttpRequest {
    // TODO: Clean-up unused properties
    val baseURL: String
    val hostname: String
    val ip: String
    val ips: Array<String>?
    val method: String
    val path: String
    val protocol: String
    val headers: ParameterMap
    val query: ParameterMap
    val body: Any?
    val bodyString: String?
}

inline fun <reified T> HttpRequest.decodeBodyFromJson(jsonSerializer: Json): Either<Throwable, T?> = try {
    Either.Right(body?.let { jsonSerializer.decodeFromString<T>(string = JSON.stringify(it)) })
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "Error decoding HTTP request body${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}