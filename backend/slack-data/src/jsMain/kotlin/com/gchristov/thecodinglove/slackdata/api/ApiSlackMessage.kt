package com.gchristov.thecodinglove.slackdata.api

import com.benasher44.uuid.uuid4
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackMessage(
    @SerialName("text") val text: String?,
    @SerialName("user_id") val userId: String?,
    @SerialName("channel") val channelId: String?,
    @SerialName("response_url") val responseUrl: String?,
    @SerialName("response_type") val responseType: String,
    @SerialName("team") val teamId: String?,
    @SerialName("as_user") val asUser: Boolean,
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

enum class ApiSlackActionName(val value: String) {
    SEND("send"),
    SHUFFLE("shuffle"),
    CANCEL("cancel");
}

object ApiSlackMessageFactory {
    private const val ButtonType = "button"
    private const val PrimaryButtonStyle = "primary"

    fun processingMessage() = ApiSlackMessage(
        text = "🔎 Hang tight, we're finding your GIF...",
        userId = null,
        channelId = null,
        responseType = "ephemeral",
        responseUrl = null,
        teamId = null,
        asUser = false,
        replaceOriginal = true,
        deleteOriginal = false,
        attachments = null,
    )

    fun cancelMessage() = ApiSlackMessage(
        text = null,
        userId = null,
        channelId = null,
        responseType = "ephemeral",
        responseUrl = null,
        teamId = null,
        asUser = false,
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
        responseType = "ephemeral",
        responseUrl = null,
        teamId = null,
        asUser = false,
        replaceOriginal = true,
        deleteOriginal = false,
        attachments = listOf(
            attachment(
                title = attachmentTitle,
                url = attachmentUrl,
                imageUrl = attachmentImageUrl,
                actions = listOf(
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SEND.value,
                        text = "Send",
                        type = ButtonType,
                        value = searchSessionId,
                        url = null,
                        style = PrimaryButtonStyle,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SHUFFLE.value,
                        text = "Shuffle",
                        type = ButtonType,
                        value = searchSessionId,
                        url = null,
                        style = null,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.CANCEL.value,
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
    ) = ApiSlackMessage(
        text = searchQuery,
        userId = null,
        channelId = null,
        responseType = "in_channel",
        responseUrl = null,
        teamId = null,
        asUser = false,
        replaceOriginal = true,
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
        title: String,
        url: String,
        imageUrl: String,
        actions: List<ApiSlackMessage.ApiAttachment.ApiAction>,
    ) = ApiSlackMessage.ApiAttachment(
        title = title,
        titleLink = url,
        text = null,
        imageUrl = imageUrl,
        footer = "Posted using /codinglove",
        callbackId = uuid4().toString(),
        color = "#1e1e1e",
        actions = actions,
    )
}