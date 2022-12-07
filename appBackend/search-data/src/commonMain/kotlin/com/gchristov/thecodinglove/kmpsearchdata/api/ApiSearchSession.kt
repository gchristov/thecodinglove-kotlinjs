package com.gchristov.thecodinglove.kmpsearchdata.api

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
data class ApiSearchSession(
    val id: String,
    val query: String,
    val totalPosts: Int?,
    // Firebase doesn't like Map<Int, List<Int>> and throws ClassCastException
    val searchHistory: Map<String, List<Int>>,
    val currentPost: ApiPost?,
    val preloadedPost: ApiPost?,
    @Serializable(with = SessionStateSerializer::class)
    val state: State
) {
    @Serializable
    sealed class State {
        @Serializable
        @SerialName("searching")
        object Searching : State()
    }
}

// Out-of-the-box sealed class serialization is a bit iffy, so this is a workaround to store it as String.
private object SessionStateSerializer : KSerializer<ApiSearchSession.State> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "ApiSearchSession.State",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): ApiSearchSession.State {
        return Json.decodeFromString(decoder.decodeString())
    }

    override fun serialize(
        encoder: Encoder,
        value: ApiSearchSession.State
    ) {
        encoder.encodeString(Json.encodeToString(value))
    }
}