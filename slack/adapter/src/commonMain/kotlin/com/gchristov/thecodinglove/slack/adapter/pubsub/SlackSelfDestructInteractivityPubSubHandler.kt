package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSelfDestructMessageEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.model.isSelfDestruct
import com.gchristov.thecodinglove.slack.domain.usecase.SlackEnsureAuthenticatedUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.DeserializationStrategy
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class SlackSelfDestructInteractivityPubSubHandler(
    override val jsonSerializer: JsonSerializer,
    private val actionName: SlackActionName,
    private val selfDestructDelay: Duration,
    private val slackEnsureAuthenticatedUseCase: SlackEnsureAuthenticatedUseCase,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val pubSubPublisher: PubSubPublisher,
    private val slackConfig: SlackConfig,
    private val analytics: Analytics,
) : PubSubHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> {
    override val dispatcher: CoroutineDispatcher get() = error("not used")
    override val log: Logger get() = error("not used")
    override val pubSubDecoder: PubSubDecoder get() = error("not used")
    override val strategy: DeserializationStrategy<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> get() = error("not used")
    override fun httpConfig() = error("not used")

    @OptIn(ExperimentalTime::class)
    override suspend fun handle(event: SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage): Either<Throwable, Unit> {
        val action = event.actions.firstOrNull { it.name == actionName.apiValue }
            ?: return Either.Right(Unit)
        analytics.sendEvent(
            clientId = event.user.id,
            name = "slack_interactivity_self_destruct",
            params = mapOf(
                "user_id" to event.user.id,
                "team_id" to event.team.id,
                "self_destruct_seconds" to selfDestructDelay.inWholeSeconds.toString(),
            ),
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
                selfDestructDelay = selfDestructDelay,
            )
        ).getOrElse { return Either.Left(it) }
        if (authResult == SlackEnsureAuthenticatedUseCase.Result.AuthenticationPromptSent) {
            return Either.Right(Unit)
        }

        val sentMessage = slackSendSearchUseCase(
            SlackSendSearchUseCase.Dto(
                userId = event.user.id,
                teamId = event.team.id,
                channelId = event.channel.id,
                responseUrl = event.responseUrl,
                searchSessionId = action.value,
                selfDestructDelay = selfDestructDelay,
            )
        ).getOrElse { return Either.Left(it) }
        if (!sentMessage.isSelfDestruct) {
            return Either.Left(Throwable("Expected a self-destructing message, but got a non-self-destructing one"))
        }

        val scheduleResult = pubSubPublisher.publishJson(
            topic = slackConfig.selfDestructMessagePubSubTopic,
            body = SlackSelfDestructMessageEvent(
                id = sentMessage.id,
                userId = sentMessage.userId,
                channelId = sentMessage.channelId,
                messageTs = sentMessage.messageTs,
            ),
            jsonSerializer = jsonSerializer,
            strategy = SlackSelfDestructMessageEvent.serializer(),
            delay = Instant.fromEpochMilliseconds(requireNotNull(sentMessage.destroyTimestamp)) - Clock.System.now(),
        )
        // Discard the scheduled Cloud Task's id - PubSubHandler.handle() only reports success/failure.
        return scheduleResult.map { }
    }
}
