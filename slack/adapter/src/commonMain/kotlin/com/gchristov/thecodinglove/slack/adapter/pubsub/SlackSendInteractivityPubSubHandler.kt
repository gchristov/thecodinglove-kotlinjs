package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.usecase.SlackEnsureAuthenticatedUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.DeserializationStrategy

internal class SlackSendInteractivityPubSubHandler(
    private val slackEnsureAuthenticatedUseCase: SlackEnsureAuthenticatedUseCase,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val analytics: Analytics,
) : PubSubHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> {
    override val dispatcher: CoroutineDispatcher get() = error("not used")
    override val jsonSerializer: JsonSerializer get() = error("not used")
    override val log: Logger get() = error("not used")
    override val pubSubDecoder: PubSubDecoder get() = error("not used")
    override val strategy: DeserializationStrategy<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> get() = error("not used")
    override fun httpConfig() = error("not used")

    override suspend fun handle(event: SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage): Either<Throwable, Unit> {
        val action = event.actions.firstOrNull { it.name == SlackActionName.SEND.apiValue }
            ?: return Either.Right(Unit)
        analytics.sendEvent(
            clientId = event.user.id,
            name = "slack_interactivity_send",
            params = mapOf("user_id" to event.user.id, "team_id" to event.team.id),
        )
        // Check auth before sending - if the user isn't authenticated, a prompt is sent instead and
        // there's nothing left to do here.
        val authResult = slackEnsureAuthenticatedUseCase(
            SlackEnsureAuthenticatedUseCase.Dto(
                userId = event.user.id,
                teamId = event.team.id,
                channelId = event.channel.id,
                responseUrl = event.responseUrl,
                searchSessionId = action.value,
                selfDestructMinutes = null,
            )
        ).getOrElse { return Either.Left(it) }
        if (authResult == SlackEnsureAuthenticatedUseCase.Result.AuthenticationPromptSent) {
            return Either.Right(Unit)
        }

        // This flow never self-destructs (selfDestructMinutes = null), so there's never anything to
        // schedule - discard the returned SlackSentMessage to satisfy PubSubHandler's Unit contract.
        return slackSendSearchUseCase(
            SlackSendSearchUseCase.Dto(
                userId = event.user.id,
                teamId = event.team.id,
                channelId = event.channel.id,
                responseUrl = event.responseUrl,
                searchSessionId = action.value,
                selfDestructMinutes = null,
            )
        ).map { }
    }
}
