package com.gchristov.thecodinglove.statistics.adapter.http

import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiStatisticsReport(
    @SerialName("messages_sent") val messagesSent: Int,
    @SerialName("active_search_sessions") val activeSearchSessions: Int,
    @SerialName("messages_self_destruct") val messagesSelfDestruct: Int,
    @SerialName("slack_active_self_destruct_messages") val slackActiveSelfDestructMessages: Int,
    @SerialName("slack_user_tokens") val slackUserTokens: Int,
    @SerialName("slack_teams") val slackTeams: Int,
) {
    companion object {
        fun of(report: StatisticsReport): ApiStatisticsReport = with(report) {
            ApiStatisticsReport(
                messagesSent = messagesSent,
                activeSearchSessions = activeSearchSessions,
                messagesSelfDestruct = messagesSelfDestruct,
                slackActiveSelfDestructMessages = slackActiveSelfDestructMessages,
                slackUserTokens = slackUserTokens,
                slackTeams = slackTeams,
            )
        }
    }
}