package com.gchristov.thecodinglove.slack

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.bodyAsJson
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.sendMessage
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.toPubSubMessage
import com.gchristov.thecodinglove.slackdata.usecase.VerifySlackRequestUseCase
import kotlinx.serialization.json.Json

class SlackSlashCommandApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val verifySlackRequestUseCase: VerifySlackRequestUseCase,
    private val slackRepository: SlackRepository,
    private val slackConfig: SlackConfig,
    private val pubSubSender: PubSubSender,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer
) {
    override fun register() {
        exports.slackSlashCommand = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ): Either<Throwable, Unit> = if (slackConfig.requestVerificationEnabled) {
        verifySlackRequestUseCase(request)
    } else {
        Either.Right(Unit)
    }.flatMap {
        // TODO: Remove
        println(JSON.stringify(request.body))
        request.bodyAsJson<ApiSlackSlashCommand>(jsonSerializer)
            .leftIfNull(default = { Exception("Request body is null") })
            .flatMap { slashCommand ->
                publishSlashCommandMessage(slashCommand)
                    .flatMap {
                        slackRepository.sendProcessingMessage(
                            text = "🔎 Hang tight, we're finding your GIF...",
                            response = response
                        )
                    }
            }
    }

    private fun publishSlashCommandMessage(slackSlashCommand: ApiSlackSlashCommand) =
        pubSubSender.sendMessage(
            topic = SlackSlashCommandPubSubService.Topic,
            body = slackSlashCommand.toPubSubMessage(),
            jsonSerializer = jsonSerializer
        )
}