package com.gchristov.thecodinglove.searchdata.api

import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
data class ApiSearchSession(
    @SerialName("id") val id: String,
    @SerialName("query") val query: String,
    @SerialName("total_posts") val totalPosts: Int?,
    // Firebase doesn't like Map<Int, List<Int>> and throws ClassCastException
    @SerialName("search_history") val searchHistory: Map<String, List<Int>>,
    @SerialName("current_post") val currentPost: ApiPost?,
    @SerialName("preloaded_post") val preloadedPost: ApiPost?,
    @SerialName("state") @Serializable(with = SessionStateSerializer::class) val state: ApiState
) {
    @Serializable
    sealed class ApiState {
        @Serializable
        @SerialName("searching")
        object ApiSearching : ApiState()
    }
}

// There's currently an issue with Firebase serialization of sealed classes.
// https://github.com/GitLiveApp/firebase-kotlin-sdk/issues/343
private object SessionStateSerializer : KSerializer<ApiSearchSession.ApiState> {
    private val jsonSerializer = DiGraph.inject<Json>()
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "ApiSearchSession.ApiState",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): ApiSearchSession.ApiState =
        jsonSerializer.decodeFromString(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: ApiSearchSession.ApiState
    ) {
        encoder.encodeString(jsonSerializer.encodeToString(value))
    }
}