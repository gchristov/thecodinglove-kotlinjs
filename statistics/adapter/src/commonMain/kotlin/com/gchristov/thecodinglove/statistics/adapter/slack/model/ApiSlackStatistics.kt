package com.gchristov.thecodinglove.statistics.adapter.slack.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiSlackStatistics(
    @SerialName("active_self_destruct_messages") val activeSelfDestructMessages: Int,
    @SerialName("users") val users: Int,
    @SerialName("teams") val teams: Int,
)