package com.gchristov.thecodinglove.slack.adapter.db.mapper

import com.gchristov.thecodinglove.slack.adapter.db.DbSlackAuthToken
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthToken

internal fun SlackAuthToken.toAuthToken() = DbSlackAuthToken(
    id = id,
    scope = scope,
    token = token,
    teamId = teamId,
    teamName = teamName,
)

internal fun DbSlackAuthToken.toAuthToken() = SlackAuthToken(
    id = id,
    scope = scope,
    token = token,
    teamId = teamId,
    teamName = teamName,
)