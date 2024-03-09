package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.PubSubSlackInteractivityMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.usecase.SlackCancelSearchUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackShuffleSearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackInteractivityPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
    private val slackCancelSearchUseCase: SlackCancelSearchUseCase,
    private val analytics: Analytics,
    pubSubDecoder: PubSubDecoder,
) : BasePubSubHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
    pubSubDecoder = pubSubDecoder,
) {
    private val tag = this::class.simpleName

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/interactivity",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = PubSubSlackInteractivityMessage.serializer(),
        )
            .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
            .flatMap {
                when (it.payload) {
                    is PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage ->
                        it.payload.handle()
                }
            }
            .fold(
                ifLeft = {
                    // Swallow but report the error, so that we can investigate. At this point, the user will be seeing
                    // a post with interactivity options, so from their POV nothing will happen, so they can re-shuffle.
                    log.error(tag, it) { "Error handling request" }
                    Either.Right(Unit)
                }, ifRight = { Either.Right(Unit) }
            )

    private suspend fun PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.handle(): Either<Throwable, Unit> {
        val sendAction = sendAction()
        val selfDestruct5MinAction = selfDestruct5MinAction()
        val shuffleAction = shuffleAction()
        val cancelAction = cancelAction()
        return when {
            sendAction != null -> {
                analytics.sendEvent(
                    clientId = user.id,
                    name = "slack_interactivity_send",
                    params = mapOf(
                        "user_id" to user.id,
                        "team_id" to team.id,
                    )
                )
                slackSendSearchUseCase.invoke(
                    SlackSendSearchUseCase.Dto(
                        userId = user.id,
                        teamId = team.id,
                        channelId = channel.id,
                        responseUrl = responseUrl,
                        searchSessionId = sendAction.value,
                    )
                )
            }

            selfDestruct5MinAction != null -> {
                analytics.sendEvent(
                    clientId = user.id,
                    name = "slack_interactivity_self_destruct",
                    params = mapOf(
                        "user_id" to user.id,
                        "team_id" to team.id,
                    )
                )
                slackSendSearchUseCase.invoke(
                    SlackSendSearchUseCase.Dto(
                        userId = user.id,
                        teamId = team.id,
                        channelId = channel.id,
                        responseUrl = responseUrl,
                        searchSessionId = selfDestruct5MinAction.value,
                        selfDestructMinutes = 5,
                    )
                )
            }

            shuffleAction != null -> {
                analytics.sendEvent(
                    clientId = user.id,
                    name = "slack_interactivity_shuffle",
                    params = mapOf(
                        "user_id" to user.id,
                        "team_id" to team.id,
                    )
                )
                slackShuffleSearchUseCase.invoke(
                    SlackShuffleSearchUseCase.Dto(
                        responseUrl = responseUrl,
                        searchSessionId = shuffleAction.value,
                    )
                )
            }

            cancelAction != null -> {
                analytics.sendEvent(
                    clientId = user.id,
                    name = "slack_interactivity_cancel",
                    params = mapOf(
                        "user_id" to user.id,
                        "team_id" to team.id,
                    )
                )
                slackCancelSearchUseCase.invoke(
                    SlackCancelSearchUseCase.Dto(
                        responseUrl = responseUrl,
                        searchSessionId = cancelAction.value,
                    )
                )
            }

            else -> {
                val error = Throwable("Unsupported interactivity message action: $actions")
                analytics.sendEvent(
                    clientId = user.id,
                    name = "slack_interactivity_error",
                    params = error.message?.let { mapOf("info" to it) }
                )
                Either.Left(error)
            }
        }
    }
}

private fun PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.sendAction() =
    actions.find { it.name == SlackActionName.SEND.apiValue }

private fun PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.selfDestruct5MinAction() =
    actions.find { it.name == SlackActionName.SELF_DESTRUCT_5_MIN.apiValue }

private fun PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.shuffleAction() =
    actions.find { it.name == SlackActionName.SHUFFLE.apiValue }

private fun PubSubSlackInteractivityMessage.InteractivityPayload.InteractiveMessage.cancelAction() =
    actions.find { it.name == SlackActionName.CANCEL.apiValue }