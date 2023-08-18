package com.gchristov.thecodinglove.slackdata.db

import com.gchristov.thecodinglove.slackdata.domain.SlackAuthToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DbSlackAuthToken(
    @SerialName("id") val id: String,
    @SerialName("scope") val scope: String,
    @SerialName("token") val token: String,
    @SerialName("team_id") val teamId: String,
    @SerialName("team_name") val teamName: String,
)

internal fun SlackAuthToken.toAuthToken() = DbSlackAuthToken(
    id = id,
    scope = scope,
    token = token,
    teamId = teamId,
    teamName = teamName,
)