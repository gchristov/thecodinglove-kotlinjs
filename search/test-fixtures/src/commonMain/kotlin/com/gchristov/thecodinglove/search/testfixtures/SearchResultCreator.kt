package com.gchristov.thecodinglove.search.testfixtures

import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase

object SearchResultCreator {
    fun validResult(
        searchSessionId: String,
        query: String
    ) = SearchUseCase.Result(
        searchSessionId = searchSessionId,
        query = query,
        post = SearchPostCreator.defaultPost(),
        totalPosts = 1,
    )
}