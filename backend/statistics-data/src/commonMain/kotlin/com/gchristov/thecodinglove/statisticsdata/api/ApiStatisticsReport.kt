package com.gchristov.thecodinglove.statisticsdata.api

import com.gchristov.thecodinglove.statisticsdata.domain.StatisticsReport
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiStatisticsReport(
    @SerialName("messages_sent") val messagesSent: Int,
    @SerialName("messages_searching") val messagesSearching: Int,
    @SerialName("messages_self_destruct") val messagesSelfDestruct: Int,
    @SerialName("active_self_destruct_messages") val activeSelfDestructMessages: Int,
    @SerialName("user_tokens") val userTokens: Int,
    @SerialName("teams") val teams: Int,
)

fun StatisticsReport.toStatisticsReport() = ApiStatisticsReport(
    messagesSent = messagesSent,
    messagesSearching = messagesSearching,
    messagesSelfDestruct = messagesSelfDestruct,
    activeSelfDestructMessages = activeSelfDestructMessages,
    userTokens = userTokens,
    teams = teams,
)