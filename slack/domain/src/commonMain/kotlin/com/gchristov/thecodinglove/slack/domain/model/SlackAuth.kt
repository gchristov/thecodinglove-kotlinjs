package com.gchristov.thecodinglove.slack.domain.model

import kotlin.time.Duration

data class SlackAuthState(
    val searchSessionId: String,
    val channelId: String,
    val teamId: String,
    val userId: String,
    val responseUrl: String,
    val selfDestructDelay: Duration?,
)
