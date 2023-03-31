package com.gchristov.thecodinglove.searchdata.model

import com.gchristov.thecodinglove.htmlparsedata.HtmlPost
import com.gchristov.thecodinglove.searchdata.db.DbPost

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

internal fun DbPost.toPost() = Post(
    title = title,
    url = url,
    imageUrl = imageUrl
)