package com.gchristov.thecodinglove.statistics.testfixtures

import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

object StatisticsReportCreator {
    fun searchStatistics(
        messagesSent: Int = 100,
        activeSearchSessions: Int = 5,
        messagesSelfDestruct: Int = 3,
    ) = StatisticsReport.SearchStatistics(
        messagesSent = messagesSent,
        activeSearchSessions = activeSearchSessions,
        messagesSelfDestruct = messagesSelfDestruct,
    )

    fun slackStatistics(
        activeSelfDestructMessages: Int = 2,
        users: Int = 50,
        teams: Int = 10,
    ) = StatisticsReport.SlackStatistics(
        activeSelfDestructMessages = activeSelfDestructMessages,
        users = users,
        teams = teams,
    )

    fun report(
        searchStatistics: StatisticsReport.SearchStatistics = searchStatistics(),
        slackStatistics: StatisticsReport.SlackStatistics = slackStatistics(),
    ) = StatisticsReport(
        searchStatistics = searchStatistics,
        slackStatistics = slackStatistics,
    )
}
