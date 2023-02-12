package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.search.usecase.SearchWithHistoryUseCase

object SearchWithHistoryResultCreator {
    fun validResult(query: String) = SearchWithHistoryUseCase.Result(
        query = query,
        totalPosts = 1,
        post = PostCreator.singlePageSinglePost()[1]!![0],
        postPage = 1,
        postIndexOnPage = 0,
        postPageSize = 1
    )
}