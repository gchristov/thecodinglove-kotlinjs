package com.gchristov.thecodinglove.slack.adapter.db.mapper

import com.gchristov.thecodinglove.slack.adapter.db.DbSlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage

internal fun SlackSentMessage.toSelfDestructMessage() = DbSlackSelfDestructMessage(
    id = id,
    userId = userId,
    searchSessionId = searchSessionId,
    destroyTimestamp = requireNotNull(destroyTimestamp) { "Only self-destructing messages can be persisted" },
    channelId = channelId,
    messageTs = messageTs,
)

internal fun DbSlackSelfDestructMessage.toSelfDestructMessage() = SlackSentMessage(
    id = id,
    userId = userId,
    searchSessionId = searchSessionId,
    destroyTimestamp = destroyTimestamp,
    channelId = channelId,
    messageTs = messageTs,
)