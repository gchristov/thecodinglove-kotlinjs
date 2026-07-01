package com.gchristov.thecodinglove.slack.adapter

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SelfDestructSlackMessageEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
internal suspend fun PubSubPublisher.scheduleSelfDestruct(
    message: SlackSelfDestructMessage,
    slackConfig: SlackConfig,
    jsonSerializer: JsonSerializer,
): Either<Throwable, String> {
    val delay = Instant.fromEpochMilliseconds(message.destroyTimestamp) - Clock.System.now()
    return publishJson(
        topic = slackConfig.selfDestructMessagePubSubTopic,
        body = SelfDestructSlackMessageEvent(
            id = message.id,
            userId = message.userId,
            channelId = message.channelId,
            messageTs = message.messageTs,
        ),
        jsonSerializer = jsonSerializer,
        strategy = SelfDestructSlackMessageEvent.serializer(),
        delay = delay,
    )
}
