package com.gchristov.thecodinglove.slack.proto.pubsub

import kotlinx.serialization.Serializable

@Serializable
data class PubSubSlackSlashCommandMessage(
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