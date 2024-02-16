package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchResult

internal fun SearchUseCase.Result.toSearchResult() = ApiSearchResult(
    searchSessionId = searchSessionId,
    query = query,
    post = post.toPost(),
    totalPosts = totalPosts
)

private fun SearchPost.toPost() = ApiSearchResult.ApiPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)