package com.gchristov.thecodinglove.slack.domain

import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.model.SlackMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackMessageResponseType

class SlackMessageFactory {
    fun message(
        text: String? = null,
        channelId: String? = null,
        responseType: SlackMessageResponseType = SlackMessageResponseType.EPHEMERAL,
        replaceOriginal: Boolean = true,
        deleteOriginal: Boolean = false,
        attachments: List<SlackMessage.Attachment>? = null,
    ) = SlackMessage(
        text = text,
        userId = null,
        channelId = channelId,
        responseType = responseType.apiValue,
        teamId = null,
        replaceOriginal = replaceOriginal,
        deleteOriginal = deleteOriginal,
        attachments = attachments,
    )

    fun cancelMessage() = message(deleteOriginal = true)

    fun searchResultMessage(
        searchQuery: String,
        searchResults: Int,
        searchSessionId: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
    ) = message(
        text = "$searchQuery - ($searchResults result${if (searchResults == 1) "" else "s"} found)",
        attachments = listOf(
            attachment(
                title = attachmentTitle,
                url = attachmentUrl,
                imageUrl = attachmentImageUrl,
                footer = PostedUsingFooter,
                actions = listOf(
                    SlackMessage.Attachment.Action(
                        name = SlackActionName.SEND.apiValue,
                        text = SlackActionName.SEND.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = ActionStylePrimary,
                    ),
                    SlackMessage.Attachment.Action(
                        name = SlackActionName.SELF_DESTRUCT_5_MIN.apiValue,
                        text = SlackActionName.SELF_DESTRUCT_5_MIN.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = ActionStylePrimary,
                    ),
                    SlackMessage.Attachment.Action(
                        name = SlackActionName.SHUFFLE.apiValue,
                        text = SlackActionName.SHUFFLE.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = null,
                    ),
                    SlackMessage.Attachment.Action(
                        name = SlackActionName.CANCEL.apiValue,
                        text = SlackActionName.CANCEL.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = null,
                    )
                ),
            )
        ),
    )

    fun authMessage(
        searchSessionId: String,
        teamId: String,
        clientId: String,
        state: String,
    ) = message(
        attachments = listOf(
            attachment(
                text = """
                The Coding Love GIFs does not have permission to post messages yet. Allowing access will post this GIF to this channel on your behalf.  
                """.trimIndent(),
                footer = "Will never post anything without your permission. <https://thecodinglove.crowdstandout.com/privacy-policy|Check out our privacy policy>",
                actions = listOf(
                    SlackMessage.Attachment.Action(
                        name = SlackActionName.AUTH_SEND.apiValue,
                        text = SlackActionName.AUTH_SEND.text,
                        type = ActionTypeButton,
                        value = null,
                        url = "https://slack.com/oauth/v2/authorize?client_id=$clientId&user_scope=chat:write&team=$teamId&state=$state",
                        style = ActionStylePrimary,
                    ),
                    SlackMessage.Attachment.Action(
                        name = SlackActionName.CANCEL.apiValue,
                        text = SlackActionName.CANCEL.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = null,
                    )
                ),
            )
        ),
    )

    fun searchPostMessage(
        searchQuery: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
        channelId: String,
        selfDestructMinutes: Int?,
    ) = message(
        text = searchQuery,
        channelId = channelId,
        responseType = SlackMessageResponseType.IN_CHANNEL,
        replaceOriginal = false,
        attachments = listOf(
            attachment(
                title = attachmentTitle,
                url = attachmentUrl,
                imageUrl = attachmentImageUrl,
                actions = emptyList(),
                footer = selfDestructMinutes?.let { "Self-destructing in ~$selfDestructMinutes minutes • $PostedUsingFooter" } ?: PostedUsingFooter,
            )
        ),
    )

    fun attachment(
        title: String? = null,
        text: String? = null,
        url: String? = null,
        footer: String? = null,
        imageUrl: String? = null,
        actions: List<SlackMessage.Attachment.Action> = emptyList(),
        color: String = "#1e1e1e",
    ) = SlackMessage.Attachment(
        title = title,
        titleLink = url,
        text = text,
        imageUrl = imageUrl,
        footer = footer,
        callbackId = uuid4().toString(),
        color = color,
        actions = actions,
    )

    companion object {
        private const val ActionTypeButton = "button"
        private const val ActionStylePrimary = "primary"

        private const val PostedUsingFooter = "Posted using /codinglove"
    }
}