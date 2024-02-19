package com.gchristov.thecodinglove.search.adapter.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSearchStatistics(
    @SerialName("messages_sent") val messagesSent: Int,
    @SerialName("active_search_sessions") val activeSearchSessions: Int,
    @SerialName("messages_self_destruct") val messagesSelfDestruct: Int,
)