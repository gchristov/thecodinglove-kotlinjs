package com.gchristov.thecodinglove.slacktestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.ParameterMap
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.kmpcommonkotlin.JsonSerializer
import kotlinx.serialization.DeserializationStrategy

@Suppress("UNCHECKED_CAST")
class FakeSlackApiRequest(
    fakeTimestamp: String? = null,
    fakeSignature: String? = null,
    fakeRawBody: String? = null
) : HttpRequest {
    override val baseURL: String = ""
    override val hostname: String = ""
    override val ip: String = ""
    override val ips: Array<String>? = null
    override val method: String = ""
    override val path: String = ""
    override val protocol: String = ""
    override val headers: ParameterMap = object : ParameterMap {
        override operator fun <T> get(key: String): T? {
            return when (key) {
                "x-slack-request-timestamp" -> fakeTimestamp as? T
                "x-slack-signature" -> fakeSignature as? T
                else -> null
            }
        }
    }
    override val query: ParameterMap = object : ParameterMap {
        override fun <T> get(key: String): T? = null
    }
    override val body: Any? = null
    override val bodyString: String? = fakeRawBody

    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = Either.Right(bodyString?.let { jsonSerializer.json.decodeFromString(strategy, it) })
}