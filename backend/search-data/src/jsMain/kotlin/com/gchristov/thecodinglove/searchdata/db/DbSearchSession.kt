package com.gchristov.thecodinglove.searchdata.db

import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import com.gchristov.thecodinglove.searchdata.model.SearchSession
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json

@Serializable
data class DbSearchSession(
    @SerialName("id") val id: String,
    @SerialName("query") val query: String,
    @SerialName("total_posts") val totalPosts: Int?,
    // Firebase doesn't like Map<Int, List<Int>> and throws ClassCastException
    @SerialName("search_history") val searchHistory: Map<String, List<Int>>,
    @SerialName("current_post") val currentPost: DbPost?,
    @SerialName("preloaded_post") val preloadedPost: DbPost?,
    @SerialName("state") @Serializable(with = SessionStateSerializer::class) val state: DbState
) {
    @Serializable
    sealed class DbState {
        @Serializable
        @SerialName("searching")
        object DbSearching : DbState()
    }
}

// There's currently an issue with Firebase serialization of sealed classes.
// https://github.com/GitLiveApp/firebase-kotlin-sdk/issues/343
private object SessionStateSerializer : KSerializer<DbSearchSession.DbState> {
    private val jsonSerializer = DiGraph.inject<Json>()
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "DbSearchSession.DbState",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): DbSearchSession.DbState =
        jsonSerializer.decodeFromString(decoder.decodeString())

    override fun serialize(
        encoder: Encoder,
        value: DbSearchSession.DbState
    ) {
        encoder.encodeString(jsonSerializer.encodeToString(value))
    }
}

internal fun SearchSession.toSearchSession() = DbSearchSession(
    id = id,
    query = query,
    totalPosts = totalPosts,
    searchHistory = mutableMapOf<String, List<Int>>().apply {
        searchHistory.keys.forEach { page ->
            searchHistory[page]?.let { itemIndex ->
                put(page.toString(), itemIndex)
            }
        }
    },
    currentPost = currentPost?.toPost(),
    preloadedPost = preloadedPost?.toPost(),
    state = state.toSearchSessionState()
)

private fun SearchSession.State.toSearchSessionState() = when (this) {
    is SearchSession.State.Searching -> DbSearchSession.DbState.DbSearching
}