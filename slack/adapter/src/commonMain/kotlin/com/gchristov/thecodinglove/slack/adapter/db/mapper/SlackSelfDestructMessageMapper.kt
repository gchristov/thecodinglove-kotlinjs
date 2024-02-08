package com.gchristov.thecodinglove.slack.adapter.db.mapper

import com.gchristov.thecodinglove.slack.adapter.db.DbSlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage

internal fun SlackSelfDestructMessage.toSelfDestructMessage() = DbSlackSelfDestructMessage(
    id = id,
    userId = userId,
    searchSessionId = searchSessionId,
    destroyTimestamp = destroyTimestamp,
    channelId = channelId,
    messageTs = messageTs,
)

internal fun DbSlackSelfDestructMessage.toSelfDestructMessage() = SlackSelfDestructMessage(
    id = id,
    userId = userId,
    searchSessionId = searchSessionId,
    destroyTimestamp = destroyTimestamp,
    channelId = channelId,
    messageTs = messageTs,
)