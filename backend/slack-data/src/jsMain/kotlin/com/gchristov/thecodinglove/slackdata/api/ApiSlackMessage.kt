package com.gchristov.thecodinglove.slackdata.api

import com.benasher44.uuid.uuid4
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackPostMessageResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
)

@Serializable
data class ApiSlackReplyWithMessageResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
)

@Serializable
data class ApiSlackMessage(
    @SerialName("text") val text: String?,
    @SerialName("user_id") val userId: String?,
    @SerialName("channel") val channelId: String?,
    @SerialName("response_url") val responseUrl: String?,
    @SerialName("response_type") val responseType: String,
    @SerialName("team") val teamId: String?,
    @SerialName("replace_original") val replaceOriginal: Boolean,
    @SerialName("delete_original") val deleteOriginal: Boolean,
    @SerialName("attachments") val attachments: List<ApiAttachment>?,
) {
    @Serializable
    data class ApiAttachment(
        @SerialName("title") val title: String?,
        @SerialName("title_link") val titleLink: String?,
        @SerialName("text") val text: String?,
        @SerialName("image_url") val imageUrl: String?,
        @SerialName("footer") val footer: String?,
        @SerialName("callback_id") val callbackId: String,
        @SerialName("color") val color: String?,
        @SerialName("actions") val actions: List<ApiAction>,
    ) {
        @Serializable
        data class ApiAction(
            @SerialName("name") val name: String,
            @SerialName("text") val text: String,
            @SerialName("type") val type: String,
            @SerialName("value") val value: String?,
            @SerialName("url") val url: String?,
            @SerialName("style") val style: String?,
        )
    }
}

enum class ApiSlackActionName(val apiValue: String) {
    SEND("send"),
    SHUFFLE("shuffle"),
    CANCEL("cancel"),
}

private enum class ApiSlackMessageResponseType(val apiValue: String) {
    EPHEMERAL("ephemeral"),
    IN_CHANNEL("in_channel"),
}

object ApiSlackMessageFactory {
    private const val ButtonType = "button"
    private const val PrimaryButtonStyle = "primary"

    fun processingMessage() = ApiSlackMessage(
        text = "🔎 Hang tight, we're finding your GIF...",
        userId = null,
        channelId = null,
        responseType = ApiSlackMessageResponseType.EPHEMERAL.apiValue,
        responseUrl = null,
        teamId = null,
        replaceOriginal = true,
        deleteOriginal = false,
        attachments = null,
    )

    fun cancelMessage() = ApiSlackMessage(
        text = null,
        userId = null,
        channelId = null,
        responseType = ApiSlackMessageResponseType.EPHEMERAL.apiValue,
        responseUrl = null,
        teamId = null,
        replaceOriginal = true,
        deleteOriginal = true,
        attachments = null,
    )

    fun searchResultMessage(
        searchQuery: String,
        searchResults: Int,
        searchSessionId: String,
        attachmentTitle: String,
        attachmentUrl: String,
        attachmentImageUrl: String,
    ) = ApiSlackMessage(
        text = "$searchQuery - ($searchResults result${if (searchResults == 1) "" else "s"} found)",
        userId = null,
        channelId = null,
        responseType = ApiSlackMessageResponseType.EPHEMERAL.apiValue,
        responseUrl = null,
        teamId = null,
        replaceOriginal = true,
        deleteOriginal = false,
        attachments = listOf(
            attachment(
                title = attachmentTitle,
                url = attachmentUrl,
                imageUrl = attachmentImageUrl,
                actions = listOf(
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SEND.apiValue,
                        text = "Send",
                        type = ButtonType,
                        value = searchSessionId,
                        url = null,
                        style = PrimaryButtonStyle,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SHUFFLE.apiValue,
                        text = "Shuffle",
                        type = ButtonType,
                        value = searchSessionId,
                        url = null,
                        style = null,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.CANCEL.apiValue,
                        text = "Cancel",
                        type = ButtonType,
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
        clientId: String
    ) = ApiSlackMessage(
        text = null,
        userId = null,
        channelId = null,
        responseType = ApiSlackMessageResponseType.EPHEMERAL.apiValue,
        responseUrl = null,
        teamId = null,
        replaceOriginal = true,
        deleteOriginal = false,
        attachments = listOf(
            attachment(
                text = "The Coding Love GIFs does not have permission to send messages on your behalf yet. Press Authorize and Send below to allow this.",
                footer = "We'll never post without your permission.",
                actions = listOf(
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SEND.apiValue,
                        text = "Authorize and Send",
                        type = ButtonType,
                        value = searchSessionId,
                        url = "https://slack.com/oauth/v2/authorize?client_id=$clientId&user_scope=chat:write&team=$teamId&state=$searchSessionId",
                        style = PrimaryButtonStyle,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.CANCEL.apiValue,
                        text = "Cancel",
                        type = ButtonType,
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
    ) = ApiSlackMessage(
        text = searchQuery,
        userId = null,
        channelId = channelId,
        responseType = ApiSlackMessageResponseType.IN_CHANNEL.apiValue,
        responseUrl = null,
        teamId = null,
        replaceOriginal = false,
        deleteOriginal = false,
        attachments = listOf(
            attachment(
                title = attachmentTitle,
                url = attachmentUrl,
                imageUrl = attachmentImageUrl,
                actions = emptyList(),
            )
        ),
    )

    private fun attachment(
        title: String? = null,
        text: String? = null,
        url: String? = null,
        footer: String? = "Posted using /codinglove",
        imageUrl: String? = null,
        actions: List<ApiSlackMessage.ApiAttachment.ApiAction>,
    ) = ApiSlackMessage.ApiAttachment(
        title = title,
        titleLink = url,
        text = text,
        imageUrl = imageUrl,
        footer = footer,
        callbackId = uuid4().toString(),
        color = "#1e1e1e",
        actions = actions,
    )
}