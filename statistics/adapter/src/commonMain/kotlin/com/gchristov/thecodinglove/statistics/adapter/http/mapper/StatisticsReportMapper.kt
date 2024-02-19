package com.gchristov.thecodinglove.statistics.adapter.http.mapper

import com.gchristov.thecodinglove.statistics.adapter.http.model.ApiStatisticsReport
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

internal fun StatisticsReport.toStatisticsReport() = ApiStatisticsReport(
    searchStatistics = ApiStatisticsReport.ApiSearchReport(
        messagesSent = searchStatistics.messagesSent,
        activeSearchSessions = searchStatistics.activeSearchSessions,
        messagesSelfDestruct = searchStatistics.messagesSelfDestruct,
    ),
    slackStatistics = ApiStatisticsReport.ApiSlackReport(
        activeSelfDestructMessages = slackStatistics.activeSelfDestructMessages,
        users = slackStatistics.users,
        teams = slackStatistics.teams,
    ),
)