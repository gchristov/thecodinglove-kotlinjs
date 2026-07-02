package com.gchristov.thecodinglove.slack.domain.model

data class SlackAuthState(
    val searchSessionId: String,
    val channelId: String,
    val teamId: String,
    val userId: String,
    val responseUrl: String,
    val selfDestructSeconds: Long?,
)
