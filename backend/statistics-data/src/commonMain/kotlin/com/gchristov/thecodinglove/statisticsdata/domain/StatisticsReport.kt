package com.gchristov.thecodinglove.statisticsdata.domain

data class StatisticsReport(
    val messagesSent: Int,
    val messagesSearching: Int,
    val messagesSelfDestruct: Int,
    val activeSelfDestructMessages: Int,
    val userTokens: Int,
    val teams: Int,
)