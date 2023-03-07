package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.*
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubTopic
import com.gchristov.thecodinglove.slackdata.api.ApiSlackActionName
import com.gchristov.thecodinglove.slackdata.domain.SlackInteractivityPubSubMessage
import com.gchristov.thecodinglove.slackdata.domain.SlackInteractivityPubSubTopic
import com.gchristov.thecodinglove.slackdata.usecase.CancelSlackSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.ShuffleSlackSearchUseCase
import kotlinx.serialization.json.Json

class SlackInteractivityPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val shuffleSlackSearchUseCase: ShuffleSlackSearchUseCase,
    private val cancelSlackSearchUseCase: CancelSlackSearchUseCase,
    private val pubSubSender: PubSubSender,
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
            .leftIfNull(default = { Exception("Message body is null") })
            .flatMap { interactivity ->
                when (interactivity.payload) {
                    is SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage ->
                        (interactivity.payload as SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage).handle()
                }
            }

    private suspend fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.handle(): Either<Throwable, Unit> {
        val shuffleAction = shuffleAction()
        val cancelAction = cancelAction()
        return when {
            shuffleAction != null -> shuffleSlackSearchUseCase.invoke(
                messageUrl = responseUrl,
                searchSessionId = shuffleAction.value
            ).flatMap { publishPreloadMessage(shuffleAction.value) }

            cancelAction != null -> cancelSlackSearchUseCase.invoke(
                messageUrl = responseUrl,
                searchSessionId = cancelAction.value
            )

            else -> Either.Left(Throwable("Unsupported interactivity message action"))
        }
    }

    private suspend fun publishPreloadMessage(searchSessionId: String) = pubSubSender.sendMessage(
        topic = PreloadPubSubTopic,
        body = PreloadPubSubMessage(searchSessionId),
        jsonSerializer = jsonSerializer,
        log = log
    )
}

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.shuffleAction() =
    actions.find { it.name == ApiSlackActionName.SHUFFLE.value }

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.cancelAction() =
    actions.find { it.name == ApiSlackActionName.CANCEL.value }