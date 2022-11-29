package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase

object SearchResultCreator {
    fun validResult(query: String) = SearchUseCase.Result.Valid(
        query = query,
        totalPosts = 1,
        post = PostCreator.singlePageSinglePost()[1]!![0],
        postPage = 1,
        postIndexOnPage = 0,
        postPageSize = 1
    )
}