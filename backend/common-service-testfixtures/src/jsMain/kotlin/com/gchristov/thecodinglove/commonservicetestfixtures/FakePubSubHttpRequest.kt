package com.gchristov.thecodinglove.commonservicetestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.ParameterMap
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubRequest
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class FakePubSubHttpRequest<T>(
    private val message: T?,
    private val messageSerializer: SerializationStrategy<T>?,
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
    override val query: ParameterMap
        get() = object : ParameterMap {
            override fun <T> get(key: String): T? = null
        }

    @ExperimentalEncodingApi
    override val body: Any? = message?.let {
        val messageJson = Json.encodeToString(requireNotNull(messageSerializer), it)
        PubSubRequest(message = PubSubRequest.Message(dataBase64 = Base64.encode(messageJson.encodeToByteArray())))
    }

    @ExperimentalEncodingApi
    override val bodyString: String? = message?.let {
        val messageJson = Json.encodeToString(requireNotNull(messageSerializer), it)
        val request =
            PubSubRequest(message = PubSubRequest.Message(dataBase64 = Base64.encode(messageJson.encodeToByteArray())))
        Json.encodeToString(request)
    }

    @ExperimentalEncodingApi
    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeBodyFromJson(
        jsonSerializer: Json,
        deserializer: DeserializationStrategy<T>
    ): Either<Throwable, T?> = Either.Right(body as T)
}