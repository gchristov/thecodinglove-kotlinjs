package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession

object SearchSessionCreator {
    fun searchSession(
        id: String,
        query: String,
        searchHistory: Map<Int, List<Int>> = emptyMap()
    ) = SearchSession(
        id = id,
        query = query,
        totalPosts = null,
        searchHistory = searchHistory,
        currentPost = null,
        state = SearchSession.State.Searching
    )
}