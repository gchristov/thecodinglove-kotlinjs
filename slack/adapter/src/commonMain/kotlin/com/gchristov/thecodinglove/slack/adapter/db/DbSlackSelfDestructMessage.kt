package com.gchristov.thecodinglove.slack.adapter.db

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DbSlackSelfDestructMessage(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("search_session_id") val searchSessionId: String,
    @SerialName("delete_timestamp") val destroyTimestamp: Long,
    @SerialName("channel_id") val channelId: String,
    @SerialName("message_ts") val messageTs: String,
)