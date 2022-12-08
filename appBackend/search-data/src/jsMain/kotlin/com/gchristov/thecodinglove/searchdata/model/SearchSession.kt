package com.gchristov.thecodinglove.searchdata.model

import com.gchristov.thecodinglove.searchdata.api.ApiPost
import com.gchristov.thecodinglove.searchdata.api.ApiSearchSession

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

internal fun ApiSearchSession.toSearchSession() = SearchSession(
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

private fun ApiPost.toPost() = Post(
    title = title,
    url = url,
    imageUrl = imageUrl
)

private fun ApiSearchSession.State.toSearchSessionState() = when (this) {
    is ApiSearchSession.State.Searching -> SearchSession.State.Searching
}

internal fun SearchSession.toApiSearchSession() = ApiSearchSession(
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
    currentPost = currentPost?.toApiPost(),
    preloadedPost = preloadedPost?.toApiPost(),
    state = state.toApiSearchSessionState()
)

private fun SearchSession.State.toApiSearchSessionState() = when (this) {
    is SearchSession.State.Searching -> ApiSearchSession.State.Searching
}

private fun Post.toApiPost() = ApiPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)