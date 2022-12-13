package com.gchristov.thecodinglove.searchdata.api

import com.gchristov.thecodinglove.searchdata.model.Post
import kotlinx.serialization.Serializable

@Serializable
data class ApiPost(
    val title: String,
    val url: String,
    val imageUrl: String,
)

internal fun Post.toPost() = ApiPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)