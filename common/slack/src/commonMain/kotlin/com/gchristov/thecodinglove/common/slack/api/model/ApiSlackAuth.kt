package com.gchristov.thecodinglove.common.slack.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSlackAuthResponse(
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
