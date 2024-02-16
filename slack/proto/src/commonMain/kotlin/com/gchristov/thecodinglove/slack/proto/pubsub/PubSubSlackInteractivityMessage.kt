package com.gchristov.thecodinglove.slack.proto.pubsub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PubSubSlackInteractivityMessage(
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