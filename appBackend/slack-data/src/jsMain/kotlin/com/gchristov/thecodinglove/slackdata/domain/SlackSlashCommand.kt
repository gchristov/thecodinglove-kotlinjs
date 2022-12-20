package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand

data class SlackSlashCommand(
    val teamId: String,
    val teamDomain: String,
    val channelId: String,
    val channelName: String,
    val userId: String,
    val userName: String,
    val command: String,
    val text: String,
    val responseUrl: String,
)

fun ApiSlackSlashCommand.toSlashCommand() = SlackSlashCommand(
    teamId = teamId,
    teamDomain = teamDomain,
    channelId = channelId,
    channelName = channelName,
    userId = userId,
    userName = userName,
    command = command,
    text = text,
    responseUrl = responseUrl
)