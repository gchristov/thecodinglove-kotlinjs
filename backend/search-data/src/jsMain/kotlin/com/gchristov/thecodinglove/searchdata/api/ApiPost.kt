package com.gchristov.thecodinglove.searchdata.api

import com.gchristov.thecodinglove.searchdata.model.Post
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiPost(
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("image_url") val imageUrl: String,
)

fun Post.toPost() = ApiPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)