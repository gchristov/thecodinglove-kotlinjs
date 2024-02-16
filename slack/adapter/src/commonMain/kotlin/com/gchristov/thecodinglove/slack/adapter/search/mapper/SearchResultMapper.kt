package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchResult
import com.gchristov.thecodinglove.slack.domain.port.SearchRepository

internal fun ApiSearchResult.toSearchResult() = SearchRepository.SearchResultDto(
    searchSessionId = searchSessionId,
    searchQuery = query,
    searchResults = totalPosts,
    attachmentTitle = post.title,
    attachmentUrl = post.url,
    attachmentImageUrl = post.imageUrl,
)