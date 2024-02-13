package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSearchResult
import com.gchristov.thecodinglove.slack.domain.port.SearchEngineDto

internal fun ApiSearchResult.toSearchResult() = SearchEngineDto(
    searchSessionId = searchSessionId,
    searchQuery = query,
    searchResults = totalPosts,
    attachmentTitle = post.title,
    attachmentUrl = post.url,
    attachmentImageUrl = post.imageUrl,
)