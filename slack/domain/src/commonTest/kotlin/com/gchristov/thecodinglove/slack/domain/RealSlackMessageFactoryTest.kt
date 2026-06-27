package com.gchristov.thecodinglove.slack.domain

import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackAuthStateSerializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RealSlackMessageFactoryTest {
    private val factory = RealSlackMessageFactory(FakeSlackAuthStateSerializer())

    @Test
    fun searchResultMessageWithSingleResultUsesSingular() {
        val message = factory.searchResultMessage(
            searchQuery = "test",
            searchResults = 1,
            searchSessionId = "session_1",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
        )
        assertEquals(expected = "test - (1 result found)", actual = message.text)
    }

    @Test
    fun searchResultMessageWithMultipleResultsUsesPlural() {
        val message = factory.searchResultMessage(
            searchQuery = "test",
            searchResults = 5,
            searchSessionId = "session_1",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
        )
        assertEquals(expected = "test - (5 results found)", actual = message.text)
    }

    @Test
    fun searchPostMessageWithSelfDestructMinutesIncludesSelfDestructInFooter() {
        val message = factory.searchPostMessage(
            searchQuery = "test",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
            channelId = "channel_1",
            selfDestructMinutes = 5,
        )
        val footer = message.attachments?.firstOrNull()?.footer
        assertNotNull(footer)
        assertTrue { footer.contains("Self-destructing in ~5 minutes") }
    }

    @Test
    fun searchPostMessageWithoutSelfDestructMinutesUsesDefaultFooter() {
        val message = factory.searchPostMessage(
            searchQuery = "test",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
            channelId = "channel_1",
            selfDestructMinutes = null,
        )
        val footer = message.attachments?.firstOrNull()?.footer
        assertEquals(expected = "Posted using /codinglove", actual = footer)
    }

    @Test
    fun authMessageContainsSerializedStateInAuthUrl() {
        val authState = SlackAuthState(
            searchSessionId = "session_1",
            channelId = "channel_1",
            teamId = "team_1",
            userId = "user_1",
            responseUrl = "https://response.url",
            selfDestructMinutes = null,
        )
        val message = factory.authMessage(clientId = "client_1", authState = authState)
        val authUrl = message.attachments?.firstOrNull()?.actions?.firstOrNull()?.url
        assertNotNull(authUrl)
        assertTrue { authUrl.contains("serialized_state") }
        assertTrue { authUrl.contains("client_1") }
    }
}
