package com.gchristov.thecodinglove.statistics.core.model

data class StatisticsReport(
    val messagesSent: Int,
    val activeSearchSessions: Int,
    val messagesSelfDestruct: Int,
    val slackActiveSelfDestructMessages: Int,
    val slackUserTokens: Int,
    val slackTeams: Int,
)