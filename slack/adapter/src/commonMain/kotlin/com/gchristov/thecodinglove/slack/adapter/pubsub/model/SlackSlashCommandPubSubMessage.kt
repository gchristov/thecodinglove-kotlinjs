package com.gchristov.thecodinglove.slack.adapter.pubsub.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SlackSlashCommandPubSubMessage(
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