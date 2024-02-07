package com.gchristov.thecodinglove.slack.domain.model

data class SlackStatistics(
    val activeSelfDestructMessages: Int,
    val users: Int,
    val teams: Int,
)