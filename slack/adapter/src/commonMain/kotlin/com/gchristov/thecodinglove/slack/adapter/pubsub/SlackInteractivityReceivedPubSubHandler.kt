package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.usecase.SlackCancelSearchUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackShuffleSearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackInteractivityReceivedPubSubHandler(
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
        path = "/api/pubsub/slack/interactivity-received",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        either {
            val body = request.decodeBodyFromJson(
                jsonSerializer = jsonSerializer,
                strategy = SlackInteractivityReceivedEvent.serializer(),
            ).bind() ?: raise(Exception("Request body is invalid"))
            val payload = body.payload as? SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage
                ?: raise(Exception("Unexpected payload type"))
            val action = payload.actions.firstOrNull()
                ?: raise(Exception("No action found"))
            when (action.name) {
                SlackActionName.SEND.apiValue, SlackActionName.SELF_DESTRUCT_5_MIN.apiValue -> {
                    val isSelfDestruct = action.name == SlackActionName.SELF_DESTRUCT_5_MIN.apiValue
                    analytics.sendEvent(
                        clientId = payload.user.id,
                        name = if (isSelfDestruct) "slack_interactivity_self_destruct" else "slack_interactivity_send",
                        params = mapOf("user_id" to payload.user.id, "team_id" to payload.team.id),
                    )
                    slackSendSearchUseCase(
                        SlackSendSearchUseCase.Dto(
                            userId = payload.user.id,
                            teamId = payload.team.id,
                            channelId = payload.channel.id,
                            responseUrl = payload.responseUrl,
                            searchSessionId = action.value,
                            selfDestructMinutes = if (isSelfDestruct) 5 else null,
                        )
                    ).bind()
                }
                SlackActionName.SHUFFLE.apiValue -> {
                    analytics.sendEvent(
                        clientId = payload.user.id,
                        name = "slack_interactivity_shuffle",
                        params = mapOf("user_id" to payload.user.id, "team_id" to payload.team.id),
                    )
                    slackShuffleSearchUseCase(
                        SlackShuffleSearchUseCase.Dto(
                            responseUrl = payload.responseUrl,
                            searchSessionId = action.value,
                        )
                    ).bind()
                }
                SlackActionName.CANCEL.apiValue -> {
                    analytics.sendEvent(
                        clientId = payload.user.id,
                        name = "slack_interactivity_cancel",
                        params = mapOf("user_id" to payload.user.id, "team_id" to payload.team.id),
                    )
                    slackCancelSearchUseCase(
                        SlackCancelSearchUseCase.Dto(
                            responseUrl = payload.responseUrl,
                            searchSessionId = action.value,
                        )
                    ).bind()
                }
            }
        }.fold(
            ifLeft = { log.error(tag, it) { "Error handling request" }; Either.Right(Unit) },
            ifRight = { Either.Right(Unit) },
        )
}
