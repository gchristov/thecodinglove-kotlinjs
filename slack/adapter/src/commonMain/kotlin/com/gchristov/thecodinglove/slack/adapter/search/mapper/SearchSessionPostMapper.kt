package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchSessionPost
import com.gchristov.thecodinglove.slack.domain.port.SearchRepository

internal fun ApiSearchSessionPost.toSearchSessionPost() = SearchRepository.SearchSessionPostDto(
    searchQuery = searchQuery,
    attachmentTitle = attachmentTitle,
    attachmentUrl = attachmentUrl,
    attachmentImageUrl = attachmentImageUrl,
)