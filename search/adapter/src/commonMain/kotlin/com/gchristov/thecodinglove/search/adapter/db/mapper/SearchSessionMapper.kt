package com.gchristov.thecodinglove.search.adapter.db.mapper

import com.gchristov.thecodinglove.search.adapter.db.DbSearchPost
import com.gchristov.thecodinglove.search.adapter.db.DbSearchSession
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchSession

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

private fun DbSearchSession.DbState.toSearchSessionState(): SearchSession.State = when (this) {
    is DbSearchSession.DbState.Searching -> SearchSession.State.Searching()
    is DbSearchSession.DbState.Sent -> SearchSession.State.Sent()
    is DbSearchSession.DbState.SelfDestruct -> SearchSession.State.SelfDestruct()
}

private fun SearchPost.toPost() = DbSearchPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)

private fun DbSearchPost.toPost() = SearchPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)