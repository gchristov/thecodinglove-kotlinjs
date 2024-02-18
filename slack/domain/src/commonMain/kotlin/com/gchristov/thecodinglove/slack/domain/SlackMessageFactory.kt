package com.gchristov.thecodinglove.slack.domain

import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackMessageResponseType
import com.gchristov.thecodinglove.slack.domain.port.SlackAuthStateSerializer

interface SlackMessageFactory {
    fun message(
        text: String? = null,
        channelId: String? = null,
        responseType: SlackMessageResponseType = SlackMessageResponseType.EPHEMERAL,
        replaceOriginal: Boolean = true,
        deleteOriginal: Boolean = false,
        attachments: List<SlackMessage.Attachment>? = null,
    ): SlackMessage

    fun searchingMessage(): SlackMessage

    fun cancelMessage(): SlackMessage

    fun searchResultMessage(
        searchQuery: String,
        searchResults: Int,
        searchSessionId: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
    ): SlackMessage

    fun authMessage(
        clientId: String,
        authState: SlackAuthState
    ): SlackMessage

    fun searchPostMessage(
        searchQuery: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
        channelId: String,
        selfDestructMinutes: Int?,
    ): SlackMessage

    fun searchGenericErrorMessage(): SlackMessage

    fun noSearchResultsMessage(query: String): SlackMessage

    fun attachment(
        title: String? = null,
        text: String? = null,
        url: String? = null,
        footer: String? = null,
        imageUrl: String? = null,
        actions: List<SlackMessage.Attachment.Action> = emptyList(),
        color: String = "#1e1e1e",
    ): SlackMessage.Attachment
}

internal class RealSlackMessageFactory(
    private val slackAuthStateSerializer: SlackAuthStateSerializer
) : SlackMessageFactory {
    override fun message(
        text: String?,
        channelId: String?,
        responseType: SlackMessageResponseType,
        replaceOriginal: Boolean,
        deleteOriginal: Boolean,
        attachments: List<SlackMessage.Attachment>?,
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

    override fun searchingMessage() = message(randomSearchingMessage())

    override fun cancelMessage() = message(deleteOriginal = true)

    override fun searchResultMessage(
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

    override fun authMessage(
        clientId: String,
        authState: SlackAuthState
    ): SlackMessage {
        val serializedAuthState = slackAuthStateSerializer.serialize(authState)
        return message(
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
                            url = "https://slack.com/oauth/v2/authorize?client_id=$clientId&user_scope=chat:write&team=${authState.teamId}&state=$serializedAuthState",
                            style = ActionStylePrimary,
                        ),
                        SlackMessage.Attachment.Action(
                            name = SlackActionName.CANCEL.apiValue,
                            text = SlackActionName.CANCEL.text,
                            type = ActionTypeButton,
                            value = authState.searchSessionId,
                            url = null,
                            style = null,
                        )
                    ),
                )
            ),
        )
    }

    override fun searchPostMessage(
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
                footer = selfDestructMinutes?.let { "Self-destructing in ~$selfDestructMinutes minutes • $PostedUsingFooter" }
                    ?: PostedUsingFooter,
            )
        ),
    )

    override fun searchGenericErrorMessage() = message(
        text = "⚠️ Something has gone wrong. We have been notified, so please try again while we investigate."
    )

    override fun noSearchResultsMessage(query: String) = message(
        text = "No results found for \"$query\". However, here are some popular suggestions for you to try again: \"release\", \"production\", \"test\""
    )

    override fun attachment(
        title: String?,
        text: String?,
        url: String?,
        footer: String?,
        imageUrl: String?,
        actions: List<SlackMessage.Attachment.Action>,
        color: String,
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

        private fun randomSearchingMessage() = listOf(
            "\uD83D\uDD75\uFE0F\u200D♀\uFE0F Hang tight, we're finding your GIF...",
            "\uD83E\uDDD0 Keep calm, we're on the hunt for your GIF...",
            "\uD83D\uDD0D Hold on, we're in pursuit of your GIF...",
            "\uD83D\uDD75\uFE0F\u200D♂\uFE0F Don't fret, we're locating your GIF...",
            "\uD83D\uDE80 Stay patient, we're rocketing to find your GIF...",
        ).random()
    }
}