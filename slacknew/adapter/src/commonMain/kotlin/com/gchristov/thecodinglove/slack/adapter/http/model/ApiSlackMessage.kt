package com.gchristov.thecodinglove.slack.adapter.http.model

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