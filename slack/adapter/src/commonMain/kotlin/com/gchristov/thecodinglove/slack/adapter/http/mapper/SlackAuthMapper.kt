package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import kotlin.time.Duration.Companion.seconds

internal fun SlackAuthState.toAuthState() = ApiSlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    selfDestructDelaySeconds = selfDestructDelay?.inWholeSeconds,
)

internal fun ApiSlackAuthState.toAuthState() = SlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    selfDestructDelay = selfDestructDelaySeconds?.seconds,
)
