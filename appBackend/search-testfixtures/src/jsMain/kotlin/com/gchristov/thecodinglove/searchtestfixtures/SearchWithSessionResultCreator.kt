package com.gchristov.thecodinglove.searchtestfixtures

import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase

object SearchWithSessionResultCreator {
    fun validResult(
        searchSessionId: String,
        query: String
    ) = SearchWithSessionUseCase.Result(
        searchSessionId = searchSessionId,
        query = query,
        post = PostCreator.defaultPost(),
        totalPosts = 1,
    )
}