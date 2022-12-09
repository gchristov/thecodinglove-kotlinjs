package com.gchristov.thecodinglove.slackdata.api

import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommand
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiSlackSlashCommand(
    @SerialName("team_id")
    val teamId: String,
    @SerialName("team_domain")
    val teamDomain: String,
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("channel_name")
    val channelName: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("user_name")
    val userName: String,
    @SerialName("command")
    val command: String,
    @SerialName("text")
    val text: String,
    @SerialName("response_url")
    val responseUrl: String,
)

// TODO: Not really needed, so remove eventually
internal fun SlackSlashCommand.toSlashCommand() = ApiSlackSlashCommand(
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