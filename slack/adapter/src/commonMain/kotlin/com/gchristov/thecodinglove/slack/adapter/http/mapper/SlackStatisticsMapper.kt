package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackStatistics
import com.gchristov.thecodinglove.slack.domain.model.SlackStatistics

internal fun SlackStatistics.toStatistics() = ApiSlackStatistics(
    activeSelfDestructMessages = activeSelfDestructMessages,
    users = users,
    teams = teams,
)