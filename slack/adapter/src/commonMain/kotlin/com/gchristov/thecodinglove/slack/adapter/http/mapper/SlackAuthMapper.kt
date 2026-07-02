package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState

internal fun SlackAuthState.toAuthState() = ApiSlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    selfDestructSeconds = selfDestructSeconds,
)

internal fun ApiSlackAuthState.toAuthState() = SlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    // self_destruct_seconds is the current field; self_destruct_minutes is decoded here only to
    // keep already-encoded (pre-deploy) state URLs working until they naturally expire.
    selfDestructSeconds = selfDestructSeconds ?: selfDestructMinutes?.let { it * 60L },
)
