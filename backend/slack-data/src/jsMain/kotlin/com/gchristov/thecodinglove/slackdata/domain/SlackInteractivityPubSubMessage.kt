package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.api.ApiSlackInteractivity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SlackInteractivityPubSubMessage(
    val payload: InteractivityPayload
) {
    @Serializable
    sealed class InteractivityPayload {
        @Serializable
        @SerialName("interactive_message")
        data class InteractiveMessage(
            val actions: List<Action>,
            val team: Team,
            val channel: Channel,
            val user: User,
            val responseUrl: String,
        ) : InteractivityPayload() {
            @Serializable
            data class Action(
                val name: String,
                val value: String,
            )

            @Serializable
            data class Team(
                val id: String,
                val domain: String,
            )

            @Serializable
            data class Channel(
                val id: String,
                val name: String,
            )

            @Serializable
            data class User(
                val id: String,
                val name: String,
            )
        }
    }
}

fun ApiSlackInteractivity.toPubSubMessage() = SlackInteractivityPubSubMessage(
    payload = payload.toPayload()
)

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.toPayload() = when (this) {
    is ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage -> SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage(
        actions = actions.map { it.toAction() },
        team = team.toTeam(),
        channel = channel.toChannel(),
        user = user.toUser(),
        responseUrl = responseUrl
    )
}

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiAction.toAction() =
    SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.Action(
        name = name,
        value = value
    )

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiTeam.toTeam() =
    SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.Team(
        id = id,
        domain = domain
    )

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiChannel.toChannel() =
    SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.Channel(
        id = id,
        name = name
    )

private fun ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiUser.toUser() =
    SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.User(
        id = id,
        name = name
    )

const val SlackInteractivityPubSubTopic = "slackInteractivityPubSub"