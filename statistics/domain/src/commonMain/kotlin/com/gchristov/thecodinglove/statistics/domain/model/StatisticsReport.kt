package com.gchristov.thecodinglove.statistics.domain.model

data class StatisticsReport(
    val searchStatistics: SearchStatistics,
    val slackStatistics: SlackStatistics,
) {
    data class SearchStatistics(
        val messagesSent: Int,
        val activeSearchSessions: Int,
        val messagesSelfDestruct: Int,
    )

    data class SlackStatistics(
        val activeSelfDestructMessages: Int,
        val users: Int,
        val teams: Int,
    )
}