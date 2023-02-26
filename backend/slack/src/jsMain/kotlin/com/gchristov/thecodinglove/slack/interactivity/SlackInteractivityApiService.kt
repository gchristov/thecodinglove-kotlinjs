package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.sendEmpty
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.sendMessage
import com.gchristov.thecodinglove.slackdata.VerifySlackRequestUseCase
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import kotlinx.serialization.json.Json

class SlackInteractivityApiService(
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
        println(JSON.stringify(request.body))
        publishInteractivityMessage()
            .flatMap {
                response.sendEmpty()
            }
    }

    private suspend fun publishInteractivityMessage() =
        pubSubSender.sendMessage(
            topic = SlackInteractivityPubSubService.Topic,
            body = "",
            jsonSerializer = jsonSerializer
        )
}