package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.pubsub.PubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.usecase.SlackShuffleSearchUseCase

internal class SlackShufflePubSubEventHandler(
    private val slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
    private val analytics: Analytics,
) : PubSubEventHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> {
    override suspend fun handle(event: SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage): Either<Throwable, Unit> {
        val action = event.actions.firstOrNull { it.name == SlackActionName.SHUFFLE.apiValue }
            ?: return Either.Right(Unit)
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
