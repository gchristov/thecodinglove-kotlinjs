package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.adapter.http.model.ApiPost
import com.gchristov.thecodinglove.search.domain.model.SearchPost

fun SearchPost.toPost() = ApiPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)