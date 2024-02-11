package com.gchristov.thecodinglove.search.domain.model

data class SearchSession(
    val id: String,
    val query: String,
    val totalPosts: Int?,
    // Contains visited page numbers mapped to visited post indexes on those pages
    val searchHistory: Map<Int, List<Int>>,
    val currentPost: SearchPost?,
    val preloadedPost: SearchPost?,
    val state: State
) {
    sealed class State {
        abstract val type: String

        data class Searching(override val type: String = "searching") : State()
        data class Sent(override val type: String = "sent") : State()
        data class SelfDestruct(override val type: String = "self-destruct") : State()
    }
}