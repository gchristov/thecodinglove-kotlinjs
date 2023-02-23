package com.gchristov.thecodinglove.slack

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.*
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.sendMessage
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.toPubSubMessage
import com.gchristov.thecodinglove.slackdata.usecase.VerifySlackRequestUseCase
import kotlinx.serialization.json.Json

class SlackSlashCommandApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val verifySlackRequestUseCase: VerifySlackRequestUseCase,
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
        println(JSON.stringify(request.body))
        request.bodyAsJson<ApiSlackSlashCommand>(jsonSerializer)
            .leftIfNull(default = { Exception("Request body is null") })
            .flatMap { slashCommand ->
                publishSlashCommandMessage(slashCommand)
                    .flatMap {
                        response.sendEmpty()
                    }
            }
    }

    private suspend fun publishSlashCommandMessage(slackSlashCommand: ApiSlackSlashCommand) =
        pubSubSender.sendMessage(
            topic = SlackSlashCommandPubSubService.Topic,
            body = slackSlashCommand.toPubSubMessage(),
            jsonSerializer = jsonSerializer
        )
}