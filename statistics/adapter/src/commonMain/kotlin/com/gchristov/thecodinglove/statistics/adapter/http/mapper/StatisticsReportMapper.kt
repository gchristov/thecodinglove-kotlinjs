package com.gchristov.thecodinglove.statistics.adapter.http.mapper

import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.proto.http.model.ApiStatisticsReport

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