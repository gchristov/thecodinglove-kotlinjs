package com.gchristov.thecodinglove.slack.adapter.db

import com.gchristov.thecodinglove.slack.domain.model.SlackAuthToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DbSlackAuthToken(
    @SerialName("id") val id: String,
    @SerialName("scope") val scope: String,
    @SerialName("token") val token: String,
    @SerialName("team_id") val teamId: String,
    @SerialName("team_name") val teamName: String,
) {
    companion object {
        fun of(authToken: SlackAuthToken) = with(authToken) {
            DbSlackAuthToken(
                id = id,
                scope = scope,
                token = token,
                teamId = teamId,
                teamName = teamName,
            )
        }
    }
}

internal fun DbSlackAuthToken.toAuthToken() = SlackAuthToken(
    id = id,
    scope = scope,
    token = token,
    teamId = teamId,
    teamName = teamName,
)