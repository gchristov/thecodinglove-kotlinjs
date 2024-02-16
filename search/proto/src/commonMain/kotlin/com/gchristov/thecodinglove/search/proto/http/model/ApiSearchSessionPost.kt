package com.gchristov.thecodinglove.search.proto.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSearchSessionPost(
    @SerialName("search_query") val searchQuery: String,
    @SerialName("attachment_title") val attachmentTitle: String,
    @SerialName("attachment_url") val attachmentUrl: String,
    @SerialName("attachment_image_url") val attachmentImageUrl: String,
)