package com.gchristov.thecodinglove.statisticsdata.domain

data class StatisticsReport(
    val messagesSent: Int,
    val activeSearchSessions: Int,
    val messagesSelfDestruct: Int,
    val slackActiveSelfDestructMessages: Int,
    val slackUserTokens: Int,
    val slackTeams: Int,
)