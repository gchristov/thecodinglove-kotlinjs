package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.model.Post
import com.gchristov.thecodinglove.searchdata.model.SearchError

interface SearchWithHistoryUseCase {
    suspend operator fun invoke(
        query: String,
        totalPosts: Int? = null,
        searchHistory: Map<Int, List<Int>>,
    ) : Either<SearchError, Result>

    data class Result(
        val query: String,
        val totalPosts: Int,
        val post: Post,
        val postPage: Int,
        val postIndexOnPage: Int,
        val postPageSize: Int
    )
}