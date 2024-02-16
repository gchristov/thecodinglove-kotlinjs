package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchSessionPost
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

internal fun ApiSearchSessionPost.toSearchSessionPost() = SlackSearchRepository.SearchSessionPostDto(
    searchQuery = searchQuery,
    attachmentTitle = attachmentTitle,
    attachmentUrl = attachmentUrl,
    attachmentImageUrl = attachmentImageUrl,
)