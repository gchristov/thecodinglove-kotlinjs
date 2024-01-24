package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthResponse
import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthState
import com.gchristov.thecodinglove.slackdata.db.DbSlackAuthToken

data class SlackAuthToken(
    val id: String,
    val scope: String,
    val token: String,
    val teamId: String,
    val teamName: String,
)

data class SlackAuthState(
    val searchSessionId: String,
    val channelId: String,
    val teamId: String,
    val userId: String,
    val responseUrl: String,
    val selfDestructMinutes: Int?,
)

// TODO: Consider better differentiation between the token types
internal fun ApiSlackAuthResponse.toAuthToken() = if (authedUser?.accessToken != null) {
    // User token
    SlackAuthToken(
        id = requireNotNull(authedUser.id),
        scope = requireNotNull(authedUser.scope),
        token = authedUser.accessToken,
        teamId = requireNotNull(team).id,
        teamName = team.name,
    )
} else {
    // Bot token
    SlackAuthToken(
        id = requireNotNull(botUserId),
        scope = requireNotNull(scope),
        token = requireNotNull(accessToken),
        teamId = requireNotNull(team).id,
        teamName = team.name,
    )
}

internal fun DbSlackAuthToken.toAuthToken() = SlackAuthToken(
    id = id,
    scope = scope,
    token = token,
    teamId = teamId,
    teamName = teamName,
)

fun ApiSlackAuthState.toAuthState() = SlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    selfDestructMinutes = selfDestructMinutes,
)