package com.gchristov.thecodinglove.slack.adapter.search.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSlackSearchSessionPost(
    @SerialName("search_query") val searchQuery: String,
    @SerialName("attachment_title") val attachmentTitle: String,
    @SerialName("attachment_url") val attachmentUrl: String,
    @SerialName("attachment_image_url") val attachmentImageUrl: String,
)