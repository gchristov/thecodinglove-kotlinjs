package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthResponse
import com.gchristov.thecodinglove.slackdata.db.DbSlackAuthToken

data class SlackAuthToken(
    val id: String,
    val scope: String,
    val token: String,
    val teamId: String,
    val teamName: String,
)

internal fun ApiSlackAuthResponse.toAuthToken() = SlackAuthToken(
    id = requireNotNull(authedUser).id,
    scope = authedUser.scope,
    token = authedUser.accessToken,
    teamId = requireNotNull(team).id,
    teamName = team.name,
)

internal fun DbSlackAuthToken.toAuthToken() = SlackAuthToken(
    id = id,
    scope = scope,
    token = token,
    teamId = teamId,
    teamName = teamName,
)