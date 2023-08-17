package com.gchristov.thecodinglove.searchdata.db

import com.gchristov.thecodinglove.searchdata.domain.Post
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DbPost(
    @SerialName("title") val title: String,
    @SerialName("url") val url: String,
    @SerialName("image_url") val imageUrl: String,
)

fun Post.toPost() = DbPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)