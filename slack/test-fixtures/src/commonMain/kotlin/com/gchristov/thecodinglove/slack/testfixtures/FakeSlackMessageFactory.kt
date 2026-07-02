package com.gchristov.thecodinglove.slack.testfixtures

import com.gchristov.thecodinglove.common.slack.model.SlackMessage
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackMessageResponseType
import kotlin.time.Duration

class FakeSlackMessageFactory : SlackMessageFactory {
    override fun message(
        text: String?,
        channelId: String?,
        responseType: SlackMessageResponseType,
        replaceOriginal: Boolean,
        deleteOriginal: Boolean,
        attachments: List<SlackMessage.Attachment>?,
    ) = dummyMessage()

    override fun searchingMessage() = dummyMessage()
    override fun cancelMessage() = dummyMessage()

    override fun searchResultMessage(
        searchQuery: String,
        searchResults: Int,
        searchSessionId: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
    ) = dummyMessage()

    override fun searchResultDelayMenuMessage(
        searchQuery: String,
        searchResults: Int,
        searchSessionId: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
    ) = dummyMessage()

    override fun authMessage(clientId: String, authState: SlackAuthState) = dummyMessage()

    override fun searchPostMessage(
        searchQuery: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
        channelId: String,
        selfDestructDelay: Duration?,
    ) = dummyMessage()

    override fun searchGenericErrorMessage() = dummyMessage()
    override fun noSearchResultsMessage(query: String) = dummyMessage()
    override fun attachment(
        title: String?,
        text: String?,
        url: String?,
        footer: String?,
        imageUrl: String?,
        actions: List<SlackMessage.Attachment.Action>,
        color: String,
    ) = SlackMessage.Attachment()
}

private fun dummyMessage() = SlackMessage()
