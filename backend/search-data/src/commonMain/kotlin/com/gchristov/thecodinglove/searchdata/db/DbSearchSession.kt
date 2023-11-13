package com.gchristov.thecodinglove.searchdata.db

import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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
    enum class DbState(val apiValue: String) {
        DbSearching("searching"),
        DbSent("sent"),
    }
}

private object SessionStateSerializer : KSerializer<DbSearchSession.DbState> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "DbSearchSession.DbState",
        kind = PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): DbSearchSession.DbState {
        val value = decoder.decodeString()
        return when (value) {
            DbSearchSession.DbState.DbSearching.apiValue -> DbSearchSession.DbState.DbSearching
            DbSearchSession.DbState.DbSent.apiValue -> DbSearchSession.DbState.DbSent
            else -> throw IllegalStateException("Unknown session state: $value")
        }
    }

    override fun serialize(
        encoder: Encoder,
        value: DbSearchSession.DbState
    ) {
        encoder.encodeString(value.apiValue)
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

private fun SearchSession.State.toSearchSessionState(): DbSearchSession.DbState = when (this) {
    SearchSession.State.Searching -> DbSearchSession.DbState.DbSearching
    SearchSession.State.Sent -> DbSearchSession.DbState.DbSent
}