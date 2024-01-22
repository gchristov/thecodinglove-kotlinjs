package com.gchristov.thecodinglove.statisticsdata.domain

data class StatisticsReport(
    val messagesSent: Int,
    val activeSearchSessions: Int,
    val messagesSelfDestruct: Int,
    val activeSelfDestructMessages: Int,
    val userTokens: Int,
    val teams: Int,
)