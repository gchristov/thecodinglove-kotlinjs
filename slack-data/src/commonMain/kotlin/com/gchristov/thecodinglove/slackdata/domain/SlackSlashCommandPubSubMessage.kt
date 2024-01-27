package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import kotlinx.serialization.Serializable

@Serializable
data class SlackSlashCommandPubSubMessage(
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

fun ApiSlackSlashCommand.toPubSubMessage() = SlackSlashCommandPubSubMessage(
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