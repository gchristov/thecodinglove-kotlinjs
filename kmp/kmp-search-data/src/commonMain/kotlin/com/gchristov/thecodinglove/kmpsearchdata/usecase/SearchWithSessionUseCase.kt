package com.gchristov.thecodinglove.kmpsearchdata.usecase

import com.gchristov.thecodinglove.kmpsearchdata.model.Post

/**
Use-case to search for a random post, wrapping it within a search session. This use-case:
- reuses or creates a new search session + search history for the given query
- searches for results for the given query, using the search session
- if the search result is valid, updates the search history
- if the search results are exhausted, clears the search history and retries the search
 */
interface SearchWithSessionUseCase {
    suspend operator fun invoke(
        searchType: SearchType,
        resultsPerPage: Int
    ): Result

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