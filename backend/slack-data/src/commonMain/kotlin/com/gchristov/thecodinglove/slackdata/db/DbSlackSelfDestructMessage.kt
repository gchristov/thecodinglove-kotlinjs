package com.gchristov.thecodinglove.slackdata.db

import com.gchristov.thecodinglove.slackdata.domain.SlackSelfDestructMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DbSlackSelfDestructMessage(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("search_session_id") val searchSessionId: String,
    @SerialName("delete_timestamp") val destroyTimestamp: Long,
    @SerialName("channel_id") val channelId: String,
    @SerialName("message_ts") val messageTs: String,
)

internal fun SlackSelfDestructMessage.toSelfDestructMessage() = DbSlackSelfDestructMessage(
    id = id,
    userId = userId,
    searchSessionId = searchSessionId,
    destroyTimestamp = destroyTimestamp,
    channelId = channelId,
    messageTs = messageTs,
)