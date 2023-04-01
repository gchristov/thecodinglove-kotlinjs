package com.gchristov.thecodinglove.slack.slashcommand

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.*
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.sendMessage
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubTopic
import com.gchristov.thecodinglove.slackdata.domain.toPubSubMessage
import com.gchristov.thecodinglove.slackdata.usecase.SlackVerifyRequestUseCase
import kotlinx.serialization.json.Json

class SlackSlashCommandApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val pubSubSender: PubSubSender,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun register() {
        exports.slackSlashCommandApi = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ): Either<Throwable, Unit> = if (slackConfig.requestVerificationEnabled) {
        slackVerifyRequestUseCase(request)
    } else {
        Either.Right(Unit)
    }.flatMap {
        request.decodeBodyFromJson<ApiSlackSlashCommand>(
            jsonSerializer = jsonSerializer,
            log = log
        )
            .leftIfNull(default = { Exception("Request body is invalid") })
            .flatMap { slashCommand ->
                publishSlashCommandMessage(slashCommand)
                    .flatMap {
                        response.sendEmptyJson(log = log)
                    }
            }
    }

    private suspend fun publishSlashCommandMessage(slashCommand: ApiSlackSlashCommand) =
        pubSubSender.sendMessage(
            topic = SlackSlashCommandPubSubTopic,
            body = slashCommand.toPubSubMessage(),
            jsonSerializer = jsonSerializer,
            log = log
        )
}