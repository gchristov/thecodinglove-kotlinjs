package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase

object SearchResultCreator {
    fun validResult(
        searchSessionId: String,
        query: String
    ) = SearchUseCase.Result(
        searchSessionId = searchSessionId,
        query = query,
        post = PostCreator.defaultPost(),
        totalPosts = 1,
    )
}