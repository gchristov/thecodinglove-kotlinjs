package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchResult
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

internal fun ApiSearchResult.toSearchResult() = SlackSearchRepository.SearchResultDto(
    ok = ok,
    error = error?.toSearchError(),
    searchSession = searchSession?.toSearchSession(),
)

private fun ApiSearchResult.ApiError.toSearchError() = when (this) {
    is ApiSearchResult.ApiError.NoResults -> SlackSearchRepository.SearchResultDto.Error.NoResults
}

private fun ApiSearchResult.ApiSearchSession.toSearchSession() = SlackSearchRepository.SearchResultDto.SearchSession(
    searchSessionId = searchSessionId,
    searchResults = totalPosts,
    post = post.toPost(query)
)

private fun ApiSearchResult.ApiPost.toPost(query: String) = SlackSearchRepository.SearchSessionPostDto(
    searchQuery = query,
    attachmentTitle = title,
    attachmentUrl = url,
    attachmentImageUrl = imageUrl,
)