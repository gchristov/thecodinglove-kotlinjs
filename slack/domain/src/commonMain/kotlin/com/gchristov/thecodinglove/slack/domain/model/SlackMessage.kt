package com.gchristov.thecodinglove.slack.domain.model

data class SlackMessage(
    val text: String?,
    val userId: String?,
    val channelId: String?,
    val responseType: String,
    val teamId: String?,
    val replaceOriginal: Boolean,
    val deleteOriginal: Boolean,
    val attachments: List<Attachment>?,
) {
    data class Attachment(
        val title: String?,
        val titleLink: String?,
        val text: String?,
        val imageUrl: String?,
        val footer: String?,
        val callbackId: String,
        val color: String?,
        val actions: List<Action>,
    ) {
        data class Action(
            val name: String,
            val text: String,
            val type: String,
            val value: String?,
            val url: String?,
            val style: String?,
        )
    }
}

enum class SlackActionName(
    val apiValue: String,
    val text: String,
) {
    AUTH_SEND(apiValue = "auth_send", text = "Allow"),
    SEND(apiValue = "send", text = "Send"),

    // TODO: Consider adding more self-destruct times here if needed
    SELF_DESTRUCT_5_MIN(apiValue = "self_destruct_5_min", text = "Send and erase in 5 minutes"),
    SHUFFLE(apiValue = "shuffle", text = "Shuffle"),
    CANCEL(apiValue = "cancel", text = "Cancel"),
}

enum class SlackMessageResponseType(val apiValue: String) {
    EPHEMERAL("ephemeral"),
    IN_CHANNEL("in_channel"),
}