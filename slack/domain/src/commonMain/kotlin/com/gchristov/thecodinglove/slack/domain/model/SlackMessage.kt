package com.gchristov.thecodinglove.slack.domain.model

enum class SlackActionName(
    val apiValue: String,
    val text: String,
) {
    AUTH_SEND(apiValue = "auth_send", text = "Allow"),
    SEND(apiValue = "send", text = "Send"),
    SELF_DESTRUCT_MENU(apiValue = "self_destruct_menu", text = "Send and Erase After..."),
    SELF_DESTRUCT_30_SEC(apiValue = "self_destruct_30_sec", text = "30 seconds"),
    SELF_DESTRUCT_1_MIN(apiValue = "self_destruct_1_min", text = "1 minute"),
    SELF_DESTRUCT_5_MIN(apiValue = "self_destruct_5_min", text = "5 minutes"),
    SELF_DESTRUCT_MENU_BACK(apiValue = "self_destruct_menu_back", text = "Cancel"),
    SHUFFLE(apiValue = "shuffle", text = "Shuffle"),
    CANCEL(apiValue = "cancel", text = "Cancel"),
}

enum class SlackMessageResponseType(val apiValue: String) {
    EPHEMERAL("ephemeral"),
    IN_CHANNEL("in_channel"),
}
