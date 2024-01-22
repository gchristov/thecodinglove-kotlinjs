package com.gchristov.thecodinglove.statisticsdata.api

import com.gchristov.thecodinglove.statisticsdata.domain.StatisticsReport
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
)

fun StatisticsReport.toStatisticsReport() = ApiStatisticsReport(
    messagesSent = messagesSent,
    activeSearchSessions = activeSearchSessions,
    messagesSelfDestruct = messagesSelfDestruct,
    slackActiveSelfDestructMessages = slackActiveSelfDestructMessages,
    slackUserTokens = slackUserTokens,
    slackTeams = slackTeams,
)