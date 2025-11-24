package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.adapter.http.model.ApiSearchResult
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase

internal fun SearchUseCase.Result.toSearchResult() = when (this) {
    is SearchUseCase.Result.Data -> ApiSearchResult(
        ok = true,
        error = null,
        searchSession = ApiSearchResult.ApiSearchSession(
            searchSessionId = searchSessionId,
            query = query,
            post = post.toPost(),
            totalPosts = totalPosts,
        )
    )
    is SearchUseCase.Result.Empty -> ApiSearchResult(
        ok = false,
        error = ApiSearchResult.ApiError.NoResults,
        searchSession = null,
    )
}

private fun SearchPost.toPost() = ApiSearchResult.ApiPost(
    title = title,
    url = url,
    imageUrl = imageUrl,
)