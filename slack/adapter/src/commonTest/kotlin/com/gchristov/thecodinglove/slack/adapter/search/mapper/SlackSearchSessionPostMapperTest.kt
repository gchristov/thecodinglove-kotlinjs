package com.gchristov.thecodinglove.slack.adapter.search.mapper

import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchSessionPost
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackSearchSessionPostMapperTest {
    @Test
    fun apiPostMappedToDomainPost() {
        assertEquals(
            expected = SlackSearchRepository.SearchSessionPostDto(
                searchQuery = "kotlin",
                attachmentTitle = "Post title",
                attachmentUrl = "https://post.url",
                attachmentImageUrl = "https://image.url",
                searchResults = 5,
            ),
            actual = ApiSlackSearchSessionPost(
                searchQuery = "kotlin",
                attachmentTitle = "Post title",
                attachmentUrl = "https://post.url",
                attachmentImageUrl = "https://image.url",
                searchResults = 5,
            ).toSearchSessionPost(),
        )
    }
}
