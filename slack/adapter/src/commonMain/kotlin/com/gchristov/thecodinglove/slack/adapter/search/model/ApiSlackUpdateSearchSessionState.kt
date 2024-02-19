package com.gchristov.thecodinglove.slack.adapter.search.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSlackUpdateSearchSessionState(
    @SerialName("search_session_id") val searchSessionId: String,
    @SerialName("state") val state: ApiState,
) {
    @Serializable
    sealed class ApiState {
        @Serializable
        @SerialName("sent")
        data object Sent : ApiState()

        @Serializable
        @SerialName("self-destruct")
        data object SelfDestruct : ApiState()
    }
}