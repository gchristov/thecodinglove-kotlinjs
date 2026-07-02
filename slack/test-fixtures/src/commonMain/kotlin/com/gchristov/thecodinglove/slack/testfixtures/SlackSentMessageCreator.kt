package com.gchristov.thecodinglove.slack.testfixtures

import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage

object SlackSentMessageCreator {
    fun message(
        id: String = "message_id",
        userId: String = "user_id",
        destroyTimestamp: Long? = null,
    ) = SlackSentMessage(
        id = id,
        userId = userId,
        searchSessionId = "session_123",
        destroyTimestamp = destroyTimestamp,
        channelId = "channel_id",
        messageTs = "message_ts",
    )

    fun pastMessage(
        id: String = "message_id",
        userId: String = "user_id",
    ) = message(id = id, userId = userId, destroyTimestamp = 0L)

    fun futureMessage(
        id: String = "message_id",
        userId: String = "user_id",
    ) = message(id = id, userId = userId, destroyTimestamp = Long.MAX_VALUE)
}
