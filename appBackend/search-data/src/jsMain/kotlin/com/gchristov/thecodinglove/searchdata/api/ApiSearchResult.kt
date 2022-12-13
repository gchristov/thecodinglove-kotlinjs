package com.gchristov.thecodinglove.searchdata.api

import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlinx.serialization.Serializable

@Serializable
data class ApiSearchResult(
    val searchSessionId: String,
    val query: String,
    val post: ApiPost,
    val totalPosts: Int
)

fun SearchWithSessionUseCase.Result.toSearchResult() = ApiSearchResult(
    searchSessionId = searchSessionId,
    query = query,
    post = post.toPost(),
    totalPosts = totalPosts
)