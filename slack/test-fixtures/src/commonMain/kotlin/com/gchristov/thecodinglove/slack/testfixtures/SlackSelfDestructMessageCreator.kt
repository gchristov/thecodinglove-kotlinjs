package com.gchristov.thecodinglove.slack.testfixtures

import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage

object SlackSelfDestructMessageCreator {
    fun pastMessage(
        id: String = "message_id",
        userId: String = "user_id",
    ) = SlackSelfDestructMessage(
        id = id,
        userId = userId,
        searchSessionId = "session_123",
        destroyTimestamp = 0L,
        channelId = "channel_id",
        messageTs = "message_ts",
    )

    fun futureMessage(
        id: String = "message_id",
        userId: String = "user_id",
    ) = SlackSelfDestructMessage(
        id = id,
        userId = userId,
        searchSessionId = "session_123",
        destroyTimestamp = Long.MAX_VALUE,
        channelId = "channel_id",
        messageTs = "message_ts",
    )
}
