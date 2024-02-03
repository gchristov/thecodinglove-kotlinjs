package com.gchristov.thecodinglove.slack.adapter.http.model

import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackAuthResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
    @SerialName("authed_user") val authedUser: ApiAuthedUser?,
    @SerialName("team") val team: ApiTeam?,
    @SerialName("scope") val scope: String?,
    @SerialName("access_token") val accessToken: String?,
    @SerialName("bot_user_id") val botUserId: String?,
) {
    @Serializable
    data class ApiAuthedUser(
        @SerialName("id") val id: String,
        @SerialName("scope") val scope: String?,
        @SerialName("access_token") val accessToken: String?,
    )

    @Serializable
    data class ApiTeam(
        @SerialName("id") val id: String,
        @SerialName("name") val name: String,
    )
}

@Serializable
data class ApiSlackAuthState(
    @SerialName("search_session_id") val searchSessionId: String,
    @SerialName("channel_id") val channelId: String,
    @SerialName("team_id") val teamId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("response_url") val responseUrl: String,
    @SerialName("self_destruct_minutes") val selfDestructMinutes: Int?,
) {
    companion object {
        fun of(authState: SlackAuthState) = with(authState) {
            ApiSlackAuthState(
                searchSessionId = searchSessionId,
                channelId = channelId,
                teamId = teamId,
                userId = userId,
                responseUrl = responseUrl,
                selfDestructMinutes = selfDestructMinutes,
            )
        }
    }
}

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

internal fun ApiSlackAuthState.toAuthState() = SlackAuthState(
    searchSessionId = searchSessionId,
    channelId = channelId,
    teamId = teamId,
    userId = userId,
    responseUrl = responseUrl,
    selfDestructMinutes = selfDestructMinutes,
)