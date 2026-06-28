package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.pubsub.PubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase

internal class SlackSelfDestructPubSubEventHandler(
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val analytics: Analytics,
) : PubSubEventHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> {
    override suspend fun handle(event: SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage): Either<Throwable, Unit> {
        val action = event.actions.firstOrNull { it.name == SlackActionName.SELF_DESTRUCT_5_MIN.apiValue }
            ?: return Either.Right(Unit)
        analytics.sendEvent(
            clientId = event.user.id,
            name = "slack_interactivity_self_destruct",
            params = mapOf("user_id" to event.user.id, "team_id" to event.team.id),
        )
        return slackSendSearchUseCase(
            SlackSendSearchUseCase.Dto(
                userId = event.user.id,
                teamId = event.team.id,
                channelId = event.channel.id,
                responseUrl = event.responseUrl,
                searchSessionId = action.value,
                selfDestructMinutes = 5,
            )
        )
    }
}
