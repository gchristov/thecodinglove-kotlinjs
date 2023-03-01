package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.*
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.sendMessage
import com.gchristov.thecodinglove.slackdata.VerifySlackRequestUseCase
import com.gchristov.thecodinglove.slackdata.api.ApiSlackInteractivity
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.toPubSubMessage
import kotlinx.serialization.json.Json

class SlackInteractivityApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val verifySlackRequestUseCase: VerifySlackRequestUseCase,
    private val slackConfig: SlackConfig,
    private val pubSubSender: PubSubSender,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun register() {
        exports.slackInteractivity = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ): Either<Throwable, Unit> = if (slackConfig.requestVerificationEnabled) {
        verifySlackRequestUseCase(request)
    } else {
        Either.Right(Unit)
    }.flatMap {
        request.decodeBodyFromJson<ApiSlackInteractivity>(
            jsonSerializer = jsonSerializer,
            log = log
        )
            .leftIfNull(default = { Exception("Request body is null") })
            .flatMap { interactivity ->
                publishInteractivityMessage(interactivity)
                    .flatMap {
                        response.sendEmpty(log = log)
                    }
            }
    }

    private suspend fun publishInteractivityMessage(interactivity: ApiSlackInteractivity) =
        pubSubSender.sendMessage(
            topic = SlackInteractivityPubSubService.Topic,
            body = interactivity.toPubSubMessage(),
            jsonSerializer = jsonSerializer,
            log = log
        )
}