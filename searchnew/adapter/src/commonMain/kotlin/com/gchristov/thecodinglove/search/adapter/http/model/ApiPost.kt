package com.gchristov.thecodinglove.search.adapter.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiPost(
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("image_url") val imageUrl: String,
)