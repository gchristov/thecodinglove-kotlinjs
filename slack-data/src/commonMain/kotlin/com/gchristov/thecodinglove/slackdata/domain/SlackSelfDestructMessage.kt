package com.gchristov.thecodinglove.slackdata.domain

import com.gchristov.thecodinglove.slackdata.db.DbSlackSelfDestructMessage

data class SlackSelfDestructMessage(
    val id: String,
    val userId: String,
    val searchSessionId: String,
    val destroyTimestamp: Long,
    val channelId: String,
    val messageTs: String,
)

internal fun DbSlackSelfDestructMessage.toSelfDestructMessage() = SlackSelfDestructMessage(
    id = id,
    userId = userId,
    searchSessionId = searchSessionId,
    destroyTimestamp = destroyTimestamp,
    channelId = channelId,
    messageTs = messageTs,
)