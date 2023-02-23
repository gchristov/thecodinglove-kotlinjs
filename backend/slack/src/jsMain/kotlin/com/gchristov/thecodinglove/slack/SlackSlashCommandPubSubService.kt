package com.gchristov.thecodinglove.slack

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.bodyAsJson
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

class SlackSlashCommandPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val slackRepository: SlackRepository
) : PubSubService(pubSubServiceRegister = pubSubServiceRegister) {
    override fun topic(): String = Topic

    override fun register() {
        exports.slackSlashCommandPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> =
        message.bodyAsJson<SlackSlashCommandPubSubMessage>(jsonSerializer)
            .leftIfNull(default = { Exception("Message body is null") })
            .flatMap { slashCommand ->
                slackRepository.sendMessage(
                    channelUrl = slashCommand.responseUrl,
                    message = ApiSlackMessage.ApiProcessing(text = "🔎 Hang tight, we're finding your GIF...")
                ).flatMap {
                    // TODO: This is temporary to prove functionality
                    delay(1000)
                    slackRepository.sendMessage(
                        channelUrl = slashCommand.responseUrl,
                        message = ApiSlackMessage.ApiProcessing(text = slashCommand.text)
                    )
                }
            }

    companion object {
        const val Topic = "slackSlashCommandPubSub"
    }
}