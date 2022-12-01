package com.gchristov.thecodinglove.kmpsearchdata.usecase

import com.gchristov.thecodinglove.kmpsearchdata.model.Post

/**
Use-case to search for a random post, wrapping it within a search session. Implementations should:
- reuse or create a new search session + search history for the given query
- search for results for the given query, using the search session
- if the search result is valid, update the search history
- if the search results are exhausted, clear the search history and retry the search
 */
interface SearchWithSessionUseCase {
    suspend operator fun invoke(searchType: SearchType): Result

    sealed class Result {
        object Empty : Result()
        data class Valid(
            val searchSessionId: String,
            val query: String,
            val post: Post,
            val totalPosts: Int
        ) : Result()
    }
}

sealed class SearchType {
    abstract val query: String

    data class WithSessionId(
        override val query: String,
        val sessionId: String
    ) : SearchType()

    data class NewSession(
        override val query: String,
    ) : SearchType()
}