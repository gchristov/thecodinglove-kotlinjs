package com.gchristov.thecodinglove.searchdata.api

import kotlinx.serialization.Serializable

@Serializable
data class ApiSearchResult(
    val searchSessionId: String,
    val query: String,
    val post: ApiPost,
    val totalPosts: Int
)