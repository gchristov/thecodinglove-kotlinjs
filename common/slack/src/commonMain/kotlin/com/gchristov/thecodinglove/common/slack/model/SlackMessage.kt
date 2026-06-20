package com.gchristov.thecodinglove.common.slack.model

data class SlackMessage(
    val text: String? = null,
    val userId: String? = null,
    val channelId: String? = null,
    val responseType: String = "",
    val teamId: String? = null,
    val replaceOriginal: Boolean = false,
    val deleteOriginal: Boolean = false,
    val attachments: List<Attachment>? = null,
) {
    data class Attachment(
        val title: String? = null,
        val titleLink: String? = null,
        val text: String? = null,
        val imageUrl: String? = null,
        val footer: String? = null,
        val callbackId: String = "",
        val color: String? = null,
        val actions: List<Action> = emptyList(),
    ) {
        data class Action(
            val name: String,
            val text: String,
            val type: String,
            val value: String? = null,
            val url: String? = null,
            val style: String? = null,
        )
    }
}
