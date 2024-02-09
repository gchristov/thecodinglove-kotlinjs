package com.gchristov.thecodinglove.slack.adapter.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DbSlackAuthToken(
    @SerialName("id") val id: String,
    @SerialName("scope") val scope: String,
    @SerialName("token") val token: String,
    @SerialName("team_id") val teamId: String,
    @SerialName("team_name") val teamName: String,
)