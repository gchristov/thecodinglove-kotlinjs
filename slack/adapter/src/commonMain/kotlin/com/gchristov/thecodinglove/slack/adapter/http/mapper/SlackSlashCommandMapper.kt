package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.PubSubSlackSlashCommandMessage

internal fun ApiSlackSlashCommand.toPubSubMessage() = PubSubSlackSlashCommandMessage(
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