package com.gchristov.thecodinglove.slack.event

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.*
import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.slackdata.api.ApiSlackEvent
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackEvent
import com.gchristov.thecodinglove.slackdata.domain.toEvent
import com.gchristov.thecodinglove.slackdata.usecase.SlackRevokeTokensUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackVerifyRequestUseCase
import kotlinx.serialization.json.Json

class SlackEventApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val slackRevokeTokensUseCase: SlackRevokeTokensUseCase,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun register() {
        exports.slackEventApi = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ): Either<Throwable, Unit> = if (slackConfig.requestVerificationEnabled) {
        slackVerifyRequestUseCase(request)
    } else {
        Either.Right(Unit)
    }.flatMap {
        request.decodeBodyFromJson<ApiSlackEvent>(
            jsonSerializer = jsonSerializer,
            log = log
        )
            .leftIfNull(default = { Exception("Request body is invalid") })
            .flatMap {
                when (val event = it.toEvent()) {
                    is SlackEvent.UrlVerification -> event.handle(response)
                    is SlackEvent.Callback -> event.handle(response)
                }
            }
    }

    private fun SlackEvent.UrlVerification.handle(response: ApiResponse) = response.sendText(
        text = challenge,
        log = log,
    )

    private suspend fun SlackEvent.Callback.handle(response: ApiResponse) =
        when (val typedEvent = event) {
            is SlackEvent.Callback.Event.TokensRevoked -> slackRevokeTokensUseCase(typedEvent).flatMap {
                response.sendEmpty(log = log)
            }
        }
}