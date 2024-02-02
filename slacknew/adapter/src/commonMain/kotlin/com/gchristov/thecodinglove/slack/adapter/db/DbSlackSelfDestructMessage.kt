package com.gchristov.thecodinglove.slack.adapter.db

import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
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
) {
    companion object {
        fun of(selfDestructMessage: SlackSelfDestructMessage) = with(selfDestructMessage) {
            DbSlackSelfDestructMessage(
                id = id,
                userId = userId,
                searchSessionId = searchSessionId,
                destroyTimestamp = destroyTimestamp,
                channelId = channelId,
                messageTs = messageTs,
            )
        }
    }
}

internal fun DbSlackSelfDestructMessage.toSelfDestructMessage() = SlackSelfDestructMessage(
    id = id,
    userId = userId,
    searchSessionId = searchSessionId,
    destroyTimestamp = destroyTimestamp,
    channelId = channelId,
    messageTs = messageTs,
)