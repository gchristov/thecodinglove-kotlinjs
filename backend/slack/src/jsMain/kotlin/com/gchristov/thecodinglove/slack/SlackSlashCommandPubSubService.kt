package com.gchristov.thecodinglove.slack

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.bodyAsJson
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import kotlinx.serialization.json.Json

class SlackSlashCommandPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
) : PubSubService(pubSubServiceRegister = pubSubServiceRegister) {
    override fun topic(): String = Topic

    override fun register() {
        exports.slackSlashCommandPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> {
        return try {
            val topicMessage =
                requireNotNull(message.bodyAsJson<SlackSlashCommandPubSubMessage>(jsonSerializer))
            // TODO: Reply to slack using response_url
            println(topicMessage)
            Either.Right(Unit)
        } catch (error: Throwable) {
            Either.Left(error)
        }
    }

    companion object {
        const val Topic = "slackSlashCommandPubSub"
    }
}