package com.gchristov.thecodinglove.slack.domain.model

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
