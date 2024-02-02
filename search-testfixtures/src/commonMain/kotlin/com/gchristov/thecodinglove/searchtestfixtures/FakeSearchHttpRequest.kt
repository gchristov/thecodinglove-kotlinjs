package com.gchristov.thecodinglove.searchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.ParameterMap
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import kotlinx.serialization.DeserializationStrategy

class FakeSearchHttpRequest(
    private val fakeSearchSessionId: String? = null,
    private val fakeSearchQuery: String? = null,
) : HttpRequest {
    override val baseURL: String = ""
    override val hostname: String = ""
    override val ip: String = ""
    override val ips: Array<String>? = null
    override val method: String = ""
    override val path: String = ""
    override val protocol: String = ""
    override val headers: ParameterMap
        get() = object : ParameterMap {
            override fun <T> get(key: String): T? = null
        }

    @Suppress("UNCHECKED_CAST")
    override val query: ParameterMap
        get() = object : ParameterMap {
            override fun <T> get(key: String): T? = when (key) {
                "searchQuery" -> fakeSearchQuery as? T
                "searchSessionId" -> fakeSearchSessionId as? T
                else -> null
            }
        }
    override val body: Any? = null
    override val bodyString: String? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = Either.Right(body as T)
}