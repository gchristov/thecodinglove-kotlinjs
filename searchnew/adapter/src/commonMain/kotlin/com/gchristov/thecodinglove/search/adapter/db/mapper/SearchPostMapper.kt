package com.gchristov.thecodinglove.search.adapter.db.mapper

import com.gchristov.thecodinglove.search.adapter.db.DbSearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchPost

fun SearchPost.toPost() = DbSearchPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)

internal fun DbSearchPost.toPost() = SearchPost(
    title = title,
    url = url,
    imageUrl = imageUrl
)