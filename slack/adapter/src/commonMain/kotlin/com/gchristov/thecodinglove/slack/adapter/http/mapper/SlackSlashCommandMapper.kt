package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandPubSubMessage

internal fun ApiSlackSlashCommand.toPubSubMessage() = SlackSlashCommandPubSubMessage(
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