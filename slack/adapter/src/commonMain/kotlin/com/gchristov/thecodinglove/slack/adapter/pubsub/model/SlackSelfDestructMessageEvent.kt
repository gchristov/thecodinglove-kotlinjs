package com.gchristov.thecodinglove.slack.adapter.pubsub.model

import kotlinx.serialization.Serializable

@Serializable
data class SlackSelfDestructMessageEvent(
    val id: String,
    val userId: String,
    val channelId: String,
    val messageTs: String,
)
