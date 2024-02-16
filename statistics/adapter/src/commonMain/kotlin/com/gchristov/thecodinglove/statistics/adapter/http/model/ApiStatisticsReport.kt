package com.gchristov.thecodinglove.statistics.adapter.http.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiStatisticsReport(
    @SerialName("search_statistics") val searchStatistics: ApiSearchReport,
    @SerialName("slack_statistics") val slackStatistics: ApiSlackReport,
) {
    @Serializable
    data class ApiSearchReport(
        @SerialName("messages_sent") val messagesSent: Int,
        @SerialName("active_search_sessions") val activeSearchSessions: Int,
        @SerialName("messages_self_destruct") val messagesSelfDestruct: Int,
    )

    @Serializable
    data class ApiSlackReport(
        @SerialName("active_self_destruct_messages") val activeSelfDestructMessages: Int,
        @SerialName("users") val users: Int,
        @SerialName("teams") val teams: Int,
    )
}