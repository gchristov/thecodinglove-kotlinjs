package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchResult
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

internal fun ApiSlackSearchResult.toSearchResult() = SlackSearchRepository.SearchResultDto(
    ok = ok,
    error = error?.toSearchError(),
    searchSession = searchSession?.toSearchSession(),
)

private fun ApiSlackSearchResult.ApiError.toSearchError() = when (this) {
    is ApiSlackSearchResult.ApiError.NoResults -> SlackSearchRepository.SearchResultDto.Error.NoResults
}

private fun ApiSlackSearchResult.ApiSearchSession.toSearchSession() = SlackSearchRepository.SearchResultDto.SearchSession(
    searchSessionId = searchSessionId,
    searchResults = totalPosts,
    post = post.toPost(query)
)

private fun ApiSlackSearchResult.ApiPost.toPost(query: String) = SlackSearchRepository.SearchSessionPostDto(
    searchQuery = query,
    attachmentTitle = title,
    attachmentUrl = url,
    attachmentImageUrl = imageUrl,
)