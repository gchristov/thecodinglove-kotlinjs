package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.decodeBodyFromJson
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slackdata.api.ApiSlackActionName
import com.gchristov.thecodinglove.slackdata.domain.SlackInteractivityPubSubMessage
import com.gchristov.thecodinglove.slackdata.usecase.CancelSlackSearchUseCase
import kotlinx.serialization.json.Json

class SlackInteractivityPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
    private val cancelSlackSearchUseCase: CancelSlackSearchUseCase,
) : PubSubService(
    pubSubServiceRegister = pubSubServiceRegister,
    log = log,
) {
    override fun topic(): String = Topic

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
        val cancelAction = cancelAction()
        return when {
            cancelAction != null -> cancelSlackSearchUseCase.invoke(
                messageUrl = responseUrl,
                searchSessionId = cancelAction.value
            )

            else -> Either.Left(Throwable("Unsupported interactivity message action"))
        }
    }

    companion object {
        const val Topic = "slackInteractivityPubSub"
    }
}

private fun SlackInteractivityPubSubMessage.InteractivityPayload.InteractiveMessage.cancelAction() =
    actions.find { it.name == ApiSlackActionName.CANCEL.value }