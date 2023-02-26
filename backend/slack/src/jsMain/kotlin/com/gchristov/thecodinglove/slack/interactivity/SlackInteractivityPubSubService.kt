package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import kotlinx.serialization.json.Json

class SlackInteractivityPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val slackRepository: SlackRepository,
    private val pubSubSender: PubSubSender,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
) : PubSubService(pubSubServiceRegister = pubSubServiceRegister) {
    override fun topic(): String = Topic

    override fun register() {
        exports.slackInteractivityPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> =
        Either.Right(Unit)

    companion object {
        const val Topic = "slackInteractivityPubSub"
    }
}