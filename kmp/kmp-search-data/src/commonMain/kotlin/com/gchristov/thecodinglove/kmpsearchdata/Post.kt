package com.gchristov.thecodinglove.kmpsearchdata

import com.gchristov.thecodinglove.kmphtmlparsedata.HtmlPost

data class Post(
    val title: String,
    val url: String,
    val imageUrl: String,
)

internal fun HtmlPost.toPost() = Post(
    title = title,
    url = url,
    imageUrl = imageUrl
)