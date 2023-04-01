package com.gchristov.thecodinglove.searchdata.model

import com.gchristov.thecodinglove.searchdata.db.DbSearchSession

data class SearchSession(
    val id: String,
    val query: String,
    val totalPosts: Int?,
    // Contains visited page numbers mapped to visited post indexes on those pages
    val searchHistory: Map<Int, List<Int>>,
    val currentPost: Post?,
    val preloadedPost: Post?,
    val state: State
) {
    sealed class State {
        object Searching : State()
    }
}

internal fun DbSearchSession.toSearchSession() = SearchSession(
    id = id,
    query = query,
    totalPosts = totalPosts,
    searchHistory = mutableMapOf<Int, List<Int>>().apply {
        searchHistory.keys.forEach { page ->
            searchHistory[page]?.let { itemIndex ->
                put(page.toInt(), itemIndex)
            }
        }
    },
    currentPost = currentPost?.toPost(),
    preloadedPost = preloadedPost?.toPost(),
    state = state.toSearchSessionState()
)

private fun DbSearchSession.DbState.toSearchSessionState() = when (this) {
    is DbSearchSession.DbState.DbSearching -> SearchSession.State.Searching
}