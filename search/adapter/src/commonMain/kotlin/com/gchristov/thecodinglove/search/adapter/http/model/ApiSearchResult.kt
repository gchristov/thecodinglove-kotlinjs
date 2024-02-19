package com.gchristov.thecodinglove.search.adapter.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSearchResult(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: ApiError?,
    @SerialName("search_session") val searchSession: ApiSearchSession?,
) {
    @Serializable
    sealed class ApiError {
        @Serializable
        @SerialName("no-results")
        data object NoResults : ApiError()
    }

    @Serializable
    data class ApiSearchSession(
        @SerialName("search_session_id") val searchSessionId: String,
        @SerialName("query") val query: String,
        @SerialName("total_posts") val totalPosts: Int,
        @SerialName("post") val post: ApiPost,
    )

    @Serializable
    data class ApiPost(
        @SerialName("title") val title: String,
        @SerialName("url") val url: String,
        @SerialName("image_url") val imageUrl: String,
    )
}