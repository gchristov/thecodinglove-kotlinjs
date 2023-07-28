package com.gchristov.thecodinglove.commonservicedata.pubsub2

import arrow.core.Either
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class PubSubRequest(val message: Message) {
    @Serializable
    data class Message(
        @SerialName("data")
        val dataBase64: String
    )
}

@ExperimentalEncodingApi
inline fun <reified T> PubSubRequest.decodeMessageFromJson(jsonSerializer: Json): Either<Throwable, T> = try {
    val decodedData = Base64.decode(message.dataBase64).decodeToString()
    val decodedObject = jsonSerializer.decodeFromString<T>(decodedData)
    Either.Right(decodedObject)
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "Error decoding PubSub request message${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}