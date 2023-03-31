package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthResponse

data class SlackAuthToken(
    val userId: String,
    val scope: String,
    val token: String,
    val teamId: String,
    val teamName: String,
)

internal fun ApiSlackAuthResponse.toAuthToken() = SlackAuthToken(
    userId = requireNotNull(authedUser).id,
    scope = requireNotNull(authedUser).scope,
    token = requireNotNull(authedUser).accessToken,
    teamId = requireNotNull(team).id,
    teamName = requireNotNull(team).name,
)