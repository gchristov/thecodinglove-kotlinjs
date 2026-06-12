package com.gchristov.thecodinglove.common.slack.model

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
