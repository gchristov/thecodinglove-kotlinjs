package com.gchristov.thecodinglove.search.adapter.http.mapper

import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchSessionPost

internal fun SearchSession.toSearchSessionPost() = ApiSearchSessionPost(
    searchQuery = query,
    attachmentTitle = currentPost!!.title,
    attachmentUrl = currentPost!!.url,
    attachmentImageUrl = currentPost!!.imageUrl,
)