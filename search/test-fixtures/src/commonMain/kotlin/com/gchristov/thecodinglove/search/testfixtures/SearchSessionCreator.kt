package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchSession

object SearchSessionCreator {
    fun searchSession(
        id: String,
        query: String,
        searchHistory: Map<Int, List<Int>> = emptyMap(),
        preloadedPost: SearchPost? = null
    ) = SearchSession(
        id = id,
        query = query,
        totalPosts = null,
        searchHistory = searchHistory,
        currentPost = null,
        preloadedPost = preloadedPost,
        state = SearchSession.State.Searching()
    )
}