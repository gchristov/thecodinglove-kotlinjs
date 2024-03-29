package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthResponse
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthToken

internal fun SlackAuthState.toAuthState() = ApiSlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    selfDestructMinutes = selfDestructMinutes,
)

// TODO: Consider better differentiation between the token types
internal fun ApiSlackAuthResponse.toAuthToken() = if (authedUser?.accessToken != null) {
    // User token
    SlackAuthToken(
        id = authedUser.id,
        scope = authedUser.scope!!,
        token = authedUser.accessToken,
        teamId = team!!.id,
        teamName = team.name,
    )
} else {
    // Bot token
    SlackAuthToken(
        id = botUserId!!,
        scope = scope!!,
        token = accessToken!!,
        teamId = team!!.id,
        teamName = team.name,
    )
}

internal fun ApiSlackAuthState.toAuthState() = SlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    selfDestructMinutes = selfDestructMinutes,
)