package com.gchristov.thecodinglove.slackdata.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class ApiSlackEvent {
    @Serializable
    @SerialName("url_verification")
    data class ApiUrlVerification(
        @SerialName("challenge") val challenge: String,
    ) : ApiSlackEvent()

    @Serializable
    @SerialName("event_callback")
    data class ApiCallback(
        @SerialName("team_id") val teamId: String,
        @SerialName("event") val event: ApiEvent,
    ) : ApiSlackEvent() {
        @Serializable
        sealed class ApiEvent {
            @Serializable
            @SerialName("tokens_revoked")
            data class ApiTokensRevoked(
                @SerialName("tokens") val tokens: ApiTokens,
            ) : ApiEvent() {
                @Serializable
                data class ApiTokens(
                    @SerialName("oauth") val oAuth: List<String>?,
                    @SerialName("bot") val bot: List<String>?,
                )
            }

            @Serializable
            @SerialName("app_uninstalled")
            data object ApiAppUninstalled : ApiEvent()
        }
    }
}