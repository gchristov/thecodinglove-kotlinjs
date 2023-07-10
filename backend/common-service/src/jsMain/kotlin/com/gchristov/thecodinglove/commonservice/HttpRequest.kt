package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import co.touchlab.kermit.Logger
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

inline fun <reified T> HttpRequest.decodeBodyFromJson(
    jsonSerializer: Json,
    log: Logger
): Either<Throwable, T?> = try {
    Either.Right(body?.let { jsonSerializer.decodeFromString<T>(string = JSON.stringify(it)) })
} catch (error: Throwable) {
    log.e(error) { error.message ?: "Error during HTTP request body decode" }
    Either.Left(error)
}