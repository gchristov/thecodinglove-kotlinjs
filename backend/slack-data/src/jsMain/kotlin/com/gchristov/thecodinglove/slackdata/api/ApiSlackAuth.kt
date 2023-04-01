package com.gchristov.thecodinglove.slackdata.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackAuthResponse(
    @SerialName("ok") val ok: Boolean,
    @SerialName("error") val error: String?,
    @SerialName("authed_user") val authedUser: ApiAuthedUser?,
    @SerialName("team") val team: ApiTeam?,
) {
    @Serializable
    data class ApiAuthedUser(
        @SerialName("id") val id: String,
        @SerialName("scope") val scope: String,
        @SerialName("access_token") val accessToken: String,
    )

    @Serializable
    data class ApiTeam(
        @SerialName("id") val id: String,
        @SerialName("name") val name: String,
    )
}