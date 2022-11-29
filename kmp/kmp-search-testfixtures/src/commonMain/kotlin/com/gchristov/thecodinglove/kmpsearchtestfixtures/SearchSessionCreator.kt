package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession

object SearchSessionCreator {
    fun searchSession(query: String) = SearchSession(
        id = "search_123",
        query = query,
        totalPosts = null,
        searchHistory = emptyMap(),
        currentPost = null,
        state = SearchSession.State.Searching
    )
}