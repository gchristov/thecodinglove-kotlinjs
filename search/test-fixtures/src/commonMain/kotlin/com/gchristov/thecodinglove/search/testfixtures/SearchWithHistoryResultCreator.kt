package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.domain.usecase.SearchWithHistoryUseCase

object SearchWithHistoryResultCreator {
    fun validResult(query: String) = SearchWithHistoryUseCase.Result.Data(
        query = query,
        totalPosts = 1,
        post = SearchPostCreator.singlePageSinglePost()[1]!![0],
        postPage = 1,
        postIndexOnPage = 0,
        postPageSize = 1
    )
}