package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchResult
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackSearchResultMapperTest {
    @Test
    fun successResultMapped() {
        assertEquals(
            expected = SlackSearchRepository.SearchResultDto(
                ok = true,
                error = null,
                searchSession = SlackSearchRepository.SearchResultDto.SearchSession(
                    searchSessionId = "session_123",
                    searchResults = 42,
                    post = SlackSearchRepository.SearchSessionPostDto(
                        searchQuery = "kotlin",
                        attachmentTitle = "Post title",
                        attachmentUrl = "https://post.url",
                        attachmentImageUrl = "https://image.url",
                        totalPosts = 42,
                    ),
                ),
            ),
            actual = ApiSlackSearchResult(
                ok = true,
                error = null,
                searchSession = ApiSlackSearchResult.ApiSearchSession(
                    searchSessionId = "session_123",
                    query = "kotlin",
                    totalPosts = 42,
                    post = ApiSlackSearchResult.ApiPost(
                        title = "Post title",
                        url = "https://post.url",
                        imageUrl = "https://image.url",
                    ),
                ),
            ).toSearchResult(),
        )
    }

    @Test
    fun noResultsErrorMapped() {
        assertEquals(
            expected = SlackSearchRepository.SearchResultDto(
                ok = false,
                error = SlackSearchRepository.SearchResultDto.Error.NoResults,
                searchSession = null,
            ),
            actual = ApiSlackSearchResult(
                ok = false,
                error = ApiSlackSearchResult.ApiError.NoResults,
                searchSession = null,
            ).toSearchResult(),
        )
    }

    @Test
    fun nullSessionMapped() {
        assertEquals(
            expected = SlackSearchRepository.SearchResultDto(
                ok = false,
                error = null,
                searchSession = null,
            ),
            actual = ApiSlackSearchResult(
                ok = false,
                error = null,
                searchSession = null,
            ).toSearchResult(),
        )
    }
}
