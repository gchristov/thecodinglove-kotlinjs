package com.gchristov.thecodinglove.statistics.adapter.slack.mapper

import com.gchristov.thecodinglove.statistics.adapter.slack.model.ApiSlackStatistics
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

internal fun ApiSlackStatistics.toStatistics() = StatisticsReport.SlackStatistics(
    activeSelfDestructMessages = activeSelfDestructMessages,
    users = users,
    teams = teams,
)