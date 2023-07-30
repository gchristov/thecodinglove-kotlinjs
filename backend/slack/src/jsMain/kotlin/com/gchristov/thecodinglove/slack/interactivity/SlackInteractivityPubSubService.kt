package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.decodeBodyFromJson
import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.slackdata.api.ApiSlackActionName
import com.gchristov.thecodinglove.slackdata.domain.SlackInteractivityPubSubMessage
import com.gchristov.thecodinglove.slackdata.domain.SlackInteractivityPubSubTopic
import com.gchristov.thecodinglove.slackdata.usecase.SlackCancelSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackSendSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackShuffleSearchUseCase
import kotlinx.serialization.json.Json

class SlackInteractivityPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
    private val slackCancelSearchUseCase: SlackCancelSearchUseCase,
) : PubSubService(
    pubSubServiceRegister = pubSubServiceRegister,
    log = log,
) {
    override fun topic(): String = SlackInteractivityPubSubTopic

    override fun register() {
        exports.slackInteractivityPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> =
        message.decodeBodyFromJson<SlackInteractivityPubSubMessage>(
            jsonSerializer = jsonSerializer,
            log = log
        )
            .leftIfNull { Exception("Message body is null") }
            .flatMap { interactivity ->
                when (interactivity.payload) {
                    is SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage ->
                        (interactivity.payload as SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage).handle()
                }
            }

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

            else -> Either.Left(Throwable("Unsupported interactivity message action"))
        }
    }
}

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.sendAction() =
    actions.find { it.name == ApiSlackActionName.SEND.apiValue }

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.shuffleAction() =
    actions.find { it.name == ApiSlackActionName.SHUFFLE.apiValue }

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.cancelAction() =
    actions.find { it.name == ApiSlackActionName.CANCEL.apiValue }