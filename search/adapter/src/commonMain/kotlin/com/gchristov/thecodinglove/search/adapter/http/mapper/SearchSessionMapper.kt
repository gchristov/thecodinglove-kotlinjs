package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.adapter.http.model.ApiSearchSessionPost
import com.gchristov.thecodinglove.search.domain.model.SearchSession

internal fun SearchSession.toSearchSessionPost() = ApiSearchSessionPost(
    searchQuery = query,
    attachmentTitle = currentPost!!.title,
    attachmentUrl = currentPost!!.url,
    attachmentImageUrl = currentPost!!.imageUrl,
)