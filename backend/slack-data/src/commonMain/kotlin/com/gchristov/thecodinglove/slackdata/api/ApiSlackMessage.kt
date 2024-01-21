package com.gchristov.thecodinglove.slackdata.api

import com.benasher44.uuid.uuid4
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackPostMessageResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
    @SerialName("ts") val messageTs: String,
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

enum class ApiSlackActionName(val apiValue: String, val text: String) {
    AUTH_SEND(apiValue = "auth_send", text = "Allow"),
    SEND(apiValue = "send", text = "Send"),
    // TODO: Consider adding more self-destruct times here if needed
    SELF_DESTRUCT_5_MIN(apiValue = "self_destruct_5_min", text = "Send and erase in 5 minutes"),
    SHUFFLE(apiValue = "shuffle", text = "Shuffle"),
    CANCEL(apiValue = "cancel", text = "Cancel"),
}

enum class ApiSlackMessageResponseType(val apiValue: String) {
    EPHEMERAL("ephemeral"),
    IN_CHANNEL("in_channel"),
}

object ApiSlackMessageFactory {
    private const val ActionTypeButton = "button"
    private const val ActionStylePrimary = "primary"

    fun message(
        text: String? = null,
        channelId: String? = null,
        responseType: ApiSlackMessageResponseType = ApiSlackMessageResponseType.EPHEMERAL,
        replaceOriginal: Boolean = true,
        deleteOriginal: Boolean = false,
        attachments: List<ApiSlackMessage.ApiAttachment>? = null,
    ) = ApiSlackMessage(
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
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SEND.apiValue,
                        text = ApiSlackActionName.SEND.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = ActionStylePrimary,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SELF_DESTRUCT_5_MIN.apiValue,
                        text = ApiSlackActionName.SELF_DESTRUCT_5_MIN.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = ActionStylePrimary,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.SHUFFLE.apiValue,
                        text = ApiSlackActionName.SHUFFLE.text,
                        type = ActionTypeButton,
                        value = searchSessionId,
                        url = null,
                        style = null,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.CANCEL.apiValue,
                        text = ApiSlackActionName.CANCEL.text,
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
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.AUTH_SEND.apiValue,
                        text = ApiSlackActionName.AUTH_SEND.text,
                        type = ActionTypeButton,
                        value = null,
                        url = "https://slack.com/oauth/v2/authorize?client_id=$clientId&user_scope=chat:write&team=$teamId&state=$state",
                        style = ActionStylePrimary,
                    ),
                    ApiSlackMessage.ApiAttachment.ApiAction(
                        name = ApiSlackActionName.CANCEL.apiValue,
                        text = ApiSlackActionName.CANCEL.text,
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
        responseType = ApiSlackMessageResponseType.IN_CHANNEL,
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
        actions: List<ApiSlackMessage.ApiAttachment.ApiAction> = emptyList(),
        color: String = "#1e1e1e",
    ) = ApiSlackMessage.ApiAttachment(
        title = title,
        titleLink = url,
        text = text,
        imageUrl = imageUrl,
        footer = footer,
        callbackId = uuid4().toString(),
        color = color,
        actions = actions,
    )
}

private const val PostedUsingFooter = "Posted using /codinglove"