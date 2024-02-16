package com.gchristov.thecodinglove.search.proto.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSearchStatistics(
    @SerialName("messages_sent") val messagesSent: Int,
    @SerialName("active_search_sessions") val activeSearchSessions: Int,
    @SerialName("messages_self_destruct") val messagesSelfDestruct: Int,
)