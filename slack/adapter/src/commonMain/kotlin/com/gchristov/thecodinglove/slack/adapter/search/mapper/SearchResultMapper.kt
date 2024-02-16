package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchResult
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

internal fun ApiSearchResult.toSearchResult() = SlackSearchRepository.SearchResultDto(
    searchSessionId = searchSessionId,
    searchQuery = query,
    searchResults = totalPosts,
    attachmentTitle = post.title,
    attachmentUrl = post.url,
    attachmentImageUrl = post.imageUrl,
)