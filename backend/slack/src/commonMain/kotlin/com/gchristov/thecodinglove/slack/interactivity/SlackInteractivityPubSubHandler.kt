package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.error
import com.gchristov.thecodinglove.commonservice.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest
import com.gchristov.thecodinglove.slackdata.api.ApiSlackActionName
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackInteractivityPubSubMessage
import com.gchristov.thecodinglove.slackdata.usecase.SlackCancelSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackSendSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackShuffleSearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackInteractivityPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
    private val slackCancelSearchUseCase: SlackCancelSearchUseCase,
    pubSubDecoder: PubSubDecoder,
    private val slackConfig: SlackConfig,
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
            strategy = SlackInteractivityPubSubMessage.serializer(),
        )
            .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
            .flatMap {
                when (it.payload) {
                    is SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage ->
                        (it.payload as SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage).handle()
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

    private suspend fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.handle(): Either<Throwable, Unit> {
        val sendAction = sendAction()
        val shuffleAction = shuffleAction()
        val cancelAction = cancelAction()
        return when {
            sendAction != null -> slackSendSearchUseCase.invoke(
                userId = user.id,
                teamId = team.id,
                channelId = channel.id,
                responseUrl = responseUrl,
                searchSessionId = sendAction.value
            )

            shuffleAction != null -> slackShuffleSearchUseCase.invoke(
                responseUrl = responseUrl,
                searchSessionId = shuffleAction.value
            )

            cancelAction != null -> slackCancelSearchUseCase.invoke(
                responseUrl = responseUrl,
                searchSessionId = cancelAction.value
            )

            else -> Either.Left(Throwable("Unsupported interactivity message action: $actions"))
        }
    }
}

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.sendAction() =
    actions.find { it.name == ApiSlackActionName.SEND.apiValue }

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.shuffleAction() =
    actions.find { it.name == ApiSlackActionName.SHUFFLE.apiValue }

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.cancelAction() =
    actions.find { it.name == ApiSlackActionName.CANCEL.apiValue }