package com.gchristov.thecodinglove.searchdata.db

import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DbSearchSession(
    @SerialName("id") val id: String,
    @SerialName("query") val query: String,
    @SerialName("total_posts") val totalPosts: Int?,
    // Firebase doesn't like Map<Int, List<Int>> and throws ClassCastException
    @SerialName("search_history") val searchHistory: Map<String, List<Int>>,
    @SerialName("current_post") val currentPost: DbPost?,
    @SerialName("preloaded_post") val preloadedPost: DbPost?,
    // The search session state model changed with the introduction of self-destruct. Before it was purely a
    // string corresponding to an enum type. No migration was needed in this case because the state wasn't used.
    @SerialName("state") val state: DbState,
) {
    @Serializable
    sealed class DbState {
        @Serializable
        @SerialName("searching")
        data object Searching : DbState()

        @Serializable
        @SerialName("sent")
        data object Sent : DbState()

        @Serializable
        @SerialName("self-destruct")
        data object SelfDestruct : DbState()
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
    is SearchSession.State.Searching -> DbSearchSession.DbState.Searching
    is SearchSession.State.Sent -> DbSearchSession.DbState.Sent
    is SearchSession.State.SelfDestruct -> DbSearchSession.DbState.SelfDestruct
}