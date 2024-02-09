package com.gchristov.thecodinglove.slack.domain.model

data class SlackSelfDestructMessage(
    val id: String,
    val userId: String,
    val searchSessionId: String,
    val destroyTimestamp: Long,
    val channelId: String,
    val messageTs: String,
)