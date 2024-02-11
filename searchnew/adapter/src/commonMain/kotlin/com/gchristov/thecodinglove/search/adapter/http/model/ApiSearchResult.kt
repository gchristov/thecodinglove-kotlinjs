package com.gchristov.thecodinglove.search.adapter.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSearchResult(
    @SerialName("search_session_id") val searchSessionId: String,
    @SerialName("query") val query: String,
    @SerialName("post") val post: ApiPost,
    @SerialName("total_posts") val totalPosts: Int
)