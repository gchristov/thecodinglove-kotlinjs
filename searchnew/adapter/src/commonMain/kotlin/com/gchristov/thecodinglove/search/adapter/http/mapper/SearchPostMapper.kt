package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.adapter.http.model.ApiSearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchPost

internal fun SearchPost.toPost() = ApiSearchPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)