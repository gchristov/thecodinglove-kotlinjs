package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.search.proto.http.ApiSearchSessionPost
import com.gchristov.thecodinglove.slack.domain.port.SearchSessionPostDto

internal fun ApiSearchSessionPost.toSearchSessionPost() = SearchSessionPostDto(
    searchQuery = searchQuery,
    attachmentTitle = attachmentTitle,
    attachmentUrl = attachmentUrl,
    attachmentImageUrl = attachmentImageUrl,
)