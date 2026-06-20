package com.gchristov.thecodinglove.common.slack.api.mapper

import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackMessage
import com.gchristov.thecodinglove.common.slack.model.SlackMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackMessageMapperTest {
    @Test
    fun messageWithAttachmentsAndActionsMappedCorrectly() {
        val message = SlackMessage(
            text = "Hello",
            userId = "U123",
            channelId = "C456",
            responseType = "ephemeral",
            teamId = "T789",
            replaceOriginal = true,
            deleteOriginal = false,
            attachments = listOf(
                SlackMessage.Attachment(
                    title = "Title",
                    titleLink = "https://example.com",
                    text = "Attachment text",
                    imageUrl = "https://img.example.com/image.png",
                    footer = "Footer",
                    callbackId = "cb_1",
                    color = "#FF0000",
                    actions = listOf(
                        SlackMessage.Attachment.Action(
                            name = "send",
                            text = "Send",
                            type = "button",
                            value = "session_1",
                            style = "primary",
                        )
                    ),
                )
            ),
        )
        assertEquals(
            expected = ApiSlackMessage(
                text = "Hello",
                userId = "U123",
                channelId = "C456",
                responseType = "ephemeral",
                teamId = "T789",
                replaceOriginal = true,
                deleteOriginal = false,
                attachments = listOf(
                    ApiSlackMessage.ApiAttachment(
                        title = "Title",
                        titleLink = "https://example.com",
                        text = "Attachment text",
                        imageUrl = "https://img.example.com/image.png",
                        footer = "Footer",
                        callbackId = "cb_1",
                        color = "#FF0000",
                        actions = listOf(
                            ApiSlackMessage.ApiAttachment.ApiAction(
                                name = "send",
                                text = "Send",
                                type = "button",
                                value = "session_1",
                                url = null,
                                style = "primary",
                            )
                        ),
                    )
                ),
            ),
            actual = message.toApiSlackMessage(),
        )
    }

    @Test
    fun nullAttachmentsMappedToNull() {
        assertEquals(
            expected = null,
            actual = SlackMessage(text = "Hello").toApiSlackMessage().attachments,
        )
    }
}
