package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchSessionPost
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

internal fun ApiSlackSearchSessionPost.toSearchSessionPost() = SlackSearchRepository.SearchSessionPostDto(
    searchQuery = searchQuery,
    attachmentTitle = attachmentTitle,
    attachmentUrl = attachmentUrl,
    attachmentImageUrl = attachmentImageUrl,
)