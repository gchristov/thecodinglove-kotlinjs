package com.gchristov.thecodinglove.search.adapter.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DbSearchPost(
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("image_url") val imageUrl: String,
)