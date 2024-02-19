package com.gchristov.thecodinglove.slack.adapter.http.mapper

import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackInteractivity
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.PubSubSlackInteractivityMessage

internal fun ApiSlackInteractivity.toPubSubMessage() = PubSubSlackInteractivityMessage(
    payload = payload.toPayload()
)

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.toPayload() = when (this) {
    is ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage -> PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage(
        actions = actions.map { it.toAction() },
        team = team.toTeam(),
        channel = channel.toChannel(),
        user = user.toUser(),
        responseUrl = responseUrl
    )
}

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiAction.toAction() =
    PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.Action(
        name = name,
        value = value
    )

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiTeam.toTeam() =
    PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.Team(
        id = id,
        domain = domain
    )

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiChannel.toChannel() =
    PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.Channel(
        id = id,
        name = name
    )

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiUser.toUser() =
    PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.User(
        id = id,
        name = name
    )