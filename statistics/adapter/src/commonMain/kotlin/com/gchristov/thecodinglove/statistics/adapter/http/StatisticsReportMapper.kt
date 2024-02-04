package com.gchristov.thecodinglove.statistics.adapter.http

import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

internal fun StatisticsReport.toStatisticsReport() = ApiStatisticsReport(
    messagesSent = messagesSent,
    activeSearchSessions = activeSearchSessions,
    messagesSelfDestruct = messagesSelfDestruct,
    slackActiveSelfDestructMessages = slackActiveSelfDestructMessages,
    slackUserTokens = slackUserTokens,
    slackTeams = slackTeams,
)