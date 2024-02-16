package com.gchristov.thecodinglove.search.proto.http

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiUpdateSearchSessionState(
    @SerialName("search_session_id") val searchSessionId: String,
    @SerialName("state") val state: ApiState,
) {
    @Serializable
    sealed class ApiState {
        @Serializable
        @SerialName("searching")
        data object Searching : ApiState()

        @Serializable
        @SerialName("sent")
        data object Sent : ApiState()

        @Serializable
        @SerialName("self-destruct")
        data object SelfDestruct : ApiState()
    }
}