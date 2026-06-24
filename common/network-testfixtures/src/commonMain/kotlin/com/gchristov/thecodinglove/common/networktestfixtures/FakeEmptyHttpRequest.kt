package com.gchristov.thecodinglove.common.networktestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.ParameterMap
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import kotlinx.serialization.DeserializationStrategy

object FakeEmptyHttpRequest : HttpRequest {
    override val baseURL = ""
    override val hostname = ""
    override val ip = ""
    override val ips: Array<String>? = null
    override val method = ""
    override val path = ""
    override val protocol = ""
    override val headers: ParameterMap = object : ParameterMap { override fun <T> get(key: String): T? = null }
    override val query: ParameterMap = object : ParameterMap { override fun <T> get(key: String): T? = null }
    override val body: Any? = null
    override val bodyString: String? = null
    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>,
    ): Either<Throwable, T?> = Either.Right(null)
}
