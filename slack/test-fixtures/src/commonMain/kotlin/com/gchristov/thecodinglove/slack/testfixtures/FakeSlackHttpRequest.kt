package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.ParameterMap
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import kotlinx.serialization.DeserializationStrategy

class FakeSlackHttpRequest(
    private val fakeBody: Any? = null,
) : HttpRequest {
    override val baseURL: String = ""
    override val hostname: String = ""
    override val ip: String = ""
    override val ips: Array<String>? = null
    override val method: String = ""
    override val path: String = ""
    override val protocol: String = ""
    override val headers: ParameterMap = object : ParameterMap {
        override fun <T> get(key: String): T? = null
    }
    override val query: ParameterMap = object : ParameterMap {
        override fun <T> get(key: String): T? = null
    }
    override val body: Any? = fakeBody
    override val bodyString: String? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>,
    ): Either<Throwable, T?> = Either.Right(fakeBody as T)
}
