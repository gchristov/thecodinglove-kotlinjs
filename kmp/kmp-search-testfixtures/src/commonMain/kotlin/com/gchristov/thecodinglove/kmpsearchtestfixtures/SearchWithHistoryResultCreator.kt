package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase

object SearchWithHistoryResultCreator {
    fun validResult(query: String) = SearchWithHistoryUseCase.Result.Valid(
        query = query,
        totalPosts = 1,
        post = PostCreator.singlePageSinglePost()[1]!![0],
        postPage = 1,
        postIndexOnPage = 0,
        postPageSize = 1
    )
}