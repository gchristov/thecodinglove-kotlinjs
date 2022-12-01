package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession

object SearchSessionCreator {
    fun searchSession(
        id: String,
        query: String,
        searchHistory: Map<Int, List<Int>> = emptyMap(),
        preloadedPost: Post? = null
    ) = SearchSession(
        id = id,
        query = query,
        totalPosts = null,
        searchHistory = searchHistory,
        currentPost = null,
        preloadedPost = preloadedPost,
        state = SearchSession.State.Searching
    )
}