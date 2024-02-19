package com.gchristov.thecodinglove.statistics.adapter.slack.mapper

import com.gchristov.thecodinglove.statistics.adapter.slack.model.ApiStatisticsSlack
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

internal fun ApiStatisticsSlack.toStatistics() = StatisticsReport.SlackStatistics(
    activeSelfDestructMessages = activeSelfDestructMessages,
    users = users,
    teams = teams,
)