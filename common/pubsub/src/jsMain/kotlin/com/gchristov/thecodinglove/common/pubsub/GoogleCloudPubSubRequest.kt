package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import io.ktor.util.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.io.encoding.ExperimentalEncodingApi

internal class GoogleCloudPubSubRequest(override val bodyString: String?) : PubSubRequest {

    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = try {
        Either.Right(bodyString?.let { jsonSerializer.json.decodeFromString(strategy, it) })
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error decoding PubSub request body${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

@Serializable
internal data class GoogleCloudPubSubRequestBody(val message: Message) {
    @Serializable
    data class Message(
        @SerialName("data")
        val dataBase64: String
    )
}

@ExperimentalEncodingApi
internal fun GoogleCloudPubSubRequestBody.toPubSubRequest(): Either<Throwable, GoogleCloudPubSubRequest> = try {
    val base64Decoded = message.dataBase64.decodeBase64String()
    Either.Right(GoogleCloudPubSubRequest(bodyString = base64Decoded))
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "Error decoding base64 PubSub request body${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}