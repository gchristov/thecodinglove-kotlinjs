package com.gchristov.thecodinglove.slack.domain.model

data class SlackAuthToken(
    val id: String,
    val scope: String,
    val token: String,
    val teamId: String,
    val teamName: String,
)

data class SlackAuthState(
    val searchSessionId: String,
    val channelId: String,
    val teamId: String,
    val userId: String,
    val responseUrl: String,
    val selfDestructMinutes: Int?,
)