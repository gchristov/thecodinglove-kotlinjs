package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase

class FakeSearchUseCase(private val result: SearchUseCase.Result) : SearchUseCase {
    override suspend fun invoke(
        query: String,
        totalPosts: Int?,
        searchHistory: Map<Int, List<Int>>,
        resultsPerPage: Int
    ): SearchUseCase.Result = result
}