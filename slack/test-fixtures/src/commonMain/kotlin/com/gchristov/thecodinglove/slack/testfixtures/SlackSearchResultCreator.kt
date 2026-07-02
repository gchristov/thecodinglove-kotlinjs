package com.gchristov.thecodinglove.slack.testfixtures

import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

object SlackSearchResultCreator {
    fun success(searchSessionId: String = "session_123") = SlackSearchRepository.SearchResultDto(
        ok = true,
        error = null,
        searchSession = SlackSearchRepository.SearchResultDto.SearchSession(
            searchSessionId = searchSessionId,
            searchResults = 10,
            post = SlackSearchSessionPostCreator.post(),
        ),
    )

    fun noResults() = SlackSearchRepository.SearchResultDto(
        ok = false,
        error = SlackSearchRepository.SearchResultDto.Error.NoResults,
        searchSession = null,
    )

    fun noSession() = SlackSearchRepository.SearchResultDto(
        ok = false,
        error = null,
        searchSession = null,
    )
}

object SlackSearchSessionPostCreator {
    fun post(totalPosts: Int = 10) = SlackSearchRepository.SearchSessionPostDto(
        searchQuery = "test",
        attachmentTitle = "title",
        attachmentUrl = "url",
        attachmentImageUrl = "imageUrl",
        totalPosts = totalPosts,
    )
}
