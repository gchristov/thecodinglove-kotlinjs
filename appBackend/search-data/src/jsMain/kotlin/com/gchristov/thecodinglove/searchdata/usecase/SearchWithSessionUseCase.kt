package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.SearchError
import com.gchristov.thecodinglove.searchdata.model.Post

interface SearchWithSessionUseCase {
    suspend operator fun invoke(type: Type): Either<SearchError, Result>

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