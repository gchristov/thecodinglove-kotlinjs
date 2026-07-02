package com.gchristov.thecodinglove.slack.adapter.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSlackAuthState(
    @SerialName("search_session_id") val searchSessionId: String,
    @SerialName("channel_id") val channelId: String,
    @SerialName("team_id") val teamId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("response_url") val responseUrl: String,
    @SerialName("self_destruct_delay_seconds") val selfDestructDelaySeconds: Long?,
)
