package com.gchristov.thecodinglove.search.adapter.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DbSearchSession(
    @SerialName("id") val id: String,
    @SerialName("query") val query: String,
    @SerialName("total_posts") val totalPosts: Int?,
    // Firebase doesn't like Map<Int, List<Int>> and throws ClassCastException
    @SerialName("search_history") val searchHistory: Map<String, List<Int>>,
    @SerialName("current_post") val currentPost: DbSearchPost?,
    @SerialName("preloaded_post") val preloadedPost: DbSearchPost?,
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