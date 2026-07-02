package com.gchristov.thecodinglove.slack.domain

import com.gchristov.thecodinglove.common.slack.model.SlackMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
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
    fun searchResultMessageHasSendMenuShuffleAndCancelActions() {
        val message = factory.searchResultMessage(
            searchQuery = "test",
            searchResults = 5,
            searchSessionId = "session_1",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
        )
        assertEquals(
            expected = listOf(
                SlackMessage.Attachment.Action(
                    name = SlackActionName.SEND.apiValue,
                    text = SlackActionName.SEND.text,
                    type = "button",
                    value = "session_1",
                    style = "primary",
                ),
                SlackMessage.Attachment.Action(
                    name = SlackActionName.SELF_DESTRUCT_MENU.apiValue,
                    text = SlackActionName.SELF_DESTRUCT_MENU.text,
                    type = "button",
                    value = "session_1",
                    style = "primary",
                ),
                SlackMessage.Attachment.Action(
                    name = SlackActionName.SHUFFLE.apiValue,
                    text = SlackActionName.SHUFFLE.text,
                    type = "button",
                    value = "session_1",
                ),
                SlackMessage.Attachment.Action(
                    name = SlackActionName.CANCEL.apiValue,
                    text = SlackActionName.CANCEL.text,
                    type = "button",
                    value = "session_1",
                ),
            ),
            actual = message.attachments?.firstOrNull()?.actions,
        )
    }

    @Test
    fun searchResultDelayMenuMessageHasDelayChoicesAndBackActions() {
        val message = factory.searchResultDelayMenuMessage(
            searchQuery = "test",
            searchResults = 5,
            searchSessionId = "session_1",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
        )
        assertEquals(expected = "test - (5 results found)", actual = message.text)
        assertEquals(
            expected = listOf(
                SlackMessage.Attachment.Action(
                    name = SlackActionName.SELF_DESTRUCT_30_SEC.apiValue,
                    text = SlackActionName.SELF_DESTRUCT_30_SEC.text,
                    type = "button",
                    value = "session_1",
                    style = "primary",
                ),
                SlackMessage.Attachment.Action(
                    name = SlackActionName.SELF_DESTRUCT_1_MIN.apiValue,
                    text = SlackActionName.SELF_DESTRUCT_1_MIN.text,
                    type = "button",
                    value = "session_1",
                    style = "primary",
                ),
                SlackMessage.Attachment.Action(
                    name = SlackActionName.SELF_DESTRUCT_5_MIN.apiValue,
                    text = SlackActionName.SELF_DESTRUCT_5_MIN.text,
                    type = "button",
                    value = "session_1",
                    style = "primary",
                ),
                SlackMessage.Attachment.Action(
                    name = SlackActionName.SELF_DESTRUCT_MENU_BACK.apiValue,
                    text = SlackActionName.SELF_DESTRUCT_MENU_BACK.text,
                    type = "button",
                    value = "session_1",
                ),
            ),
            actual = message.attachments?.firstOrNull()?.actions,
        )
    }

    @Test
    fun searchPostMessageWithMinuteSelfDestructSecondsIncludesMinutesInFooter() {
        val message = factory.searchPostMessage(
            searchQuery = "test",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
            channelId = "channel_1",
            selfDestructSeconds = 300L,
        )
        val footer = message.attachments?.firstOrNull()?.footer
        assertNotNull(footer)
        assertTrue { footer.contains("Self-destructing in ~5 minutes") }
    }

    @Test
    fun searchPostMessageWithSecondSelfDestructSecondsIncludesSecondsInFooter() {
        val message = factory.searchPostMessage(
            searchQuery = "test",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
            channelId = "channel_1",
            selfDestructSeconds = 30L,
        )
        val footer = message.attachments?.firstOrNull()?.footer
        assertNotNull(footer)
        assertTrue { footer.contains("Self-destructing in ~30 seconds") }
    }

    @Test
    fun searchPostMessageWithoutSelfDestructSecondsUsesDefaultFooter() {
        val message = factory.searchPostMessage(
            searchQuery = "test",
            attachmentTitle = "title",
            attachmentUrl = "url",
            attachmentImageUrl = "imageUrl",
            channelId = "channel_1",
            selfDestructSeconds = null,
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
            selfDestructSeconds = null,
        )
        val message = factory.authMessage(clientId = "client_1", authState = authState)
        val authUrl = message.attachments?.firstOrNull()?.actions?.firstOrNull()?.url
        assertNotNull(authUrl)
        assertTrue { authUrl.contains("serialized_state") }
        assertTrue { authUrl.contains("client_1") }
    }
}
