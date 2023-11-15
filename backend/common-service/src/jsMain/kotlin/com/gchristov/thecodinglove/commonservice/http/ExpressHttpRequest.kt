package com.gchristov.thecodinglove.commonservice.http

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.ParameterMap
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import kotlinx.serialization.DeserializationStrategy

internal class ExpressHttpRequest(private val req: dynamic) : HttpRequest {
    override val baseURL: String = req.baseUrl as String
    override val hostname: String = req.hostname as String
    override val ip: String = req.ip as String
    override val ips: Array<String>? = req.ips as? Array<String>
    override val method: String = req.method as String
    override val path: String = req.path as String
    override val protocol: String = req.protocol as String
    override val headers: ParameterMap
        get() = object : ParameterMap {
            override fun <T> get(key: String): T? = req.headers[key] as? T
        }
    override val query: ParameterMap
        get() = object : ParameterMap {
            override fun <T> get(key: String): T? = req.query[key] as? T
        }
    override val body: Any? = req.body
    override val bodyString: String? = req.bodyString as? String

    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = try {
        // Express exposes "body" as a key-value map based on the middleware installed. To decode it from JSON we simply
        // convert it to String in Javascript, which should behave the same for any kind of content type.
        Either.Right(body?.let { jsonSerializer.json.decodeFromString(strategy, JSON.stringify(it)) })
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error decoding HTTP request body${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}