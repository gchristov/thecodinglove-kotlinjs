package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.SearchException
import com.gchristov.thecodinglove.searchdata.model.Post

/**
Use-case to search for a random post, wrapping it within a search session. Implementations should:
- reuse or create a new search session + search history for the given query
- search for results for the given query, using the search session
- if the search result is valid, update the search history
- if the search results are exhausted, clear the search history and retry the search
 */
interface SearchWithSessionUseCase {
    suspend operator fun invoke(type: Type): Either<SearchException, Result>

    sealed class Type {
        abstract val query: String

        data class WithSessionId(
            override val query: String,
            val sessionId: String
        ) : Type()

        data class NewSession(
            override val query: String,
        ) : Type()
    }

    data class Result(
        val searchSessionId: String,
        val query: String,
        val post: Post,
        val totalPosts: Int
    )
}