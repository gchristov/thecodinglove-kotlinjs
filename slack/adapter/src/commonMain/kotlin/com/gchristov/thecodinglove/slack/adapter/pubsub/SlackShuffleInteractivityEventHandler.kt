package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.pubsub.PubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.usecase.SlackShuffleSearchUseCase

internal class SlackShuffleInteractivityEventHandler(
    private val slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
    private val analytics: Analytics,
) : PubSubEventHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> {
    override fun canHandle(event: SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage) =
        event.actions.any { it.name == SlackActionName.SHUFFLE.apiValue }

    override suspend fun handle(event: SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage): Either<Throwable, Unit> {
        val action = event.actions.first { it.name == SlackActionName.SHUFFLE.apiValue }
        analytics.sendEvent(
            clientId = event.user.id,
            name = "slack_interactivity_shuffle",
            params = mapOf("user_id" to event.user.id, "team_id" to event.team.id),
        )
        return slackShuffleSearchUseCase(
            SlackShuffleSearchUseCase.Dto(
                responseUrl = event.responseUrl,
                searchSessionId = action.value,
            )
        )
    }
}
