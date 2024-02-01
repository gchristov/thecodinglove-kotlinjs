package com.gchristov.thecodinglove.common.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.adapter.ParameterMap
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import kotlinx.serialization.DeserializationStrategy

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

    fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?>
}