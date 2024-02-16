package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.domain.model.SlackStatistics
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackStatistics

internal fun SlackStatistics.toStatistics() = ApiSlackStatistics(
    activeSelfDestructMessages = activeSelfDestructMessages,
    users = users,
    teams = teams,
)