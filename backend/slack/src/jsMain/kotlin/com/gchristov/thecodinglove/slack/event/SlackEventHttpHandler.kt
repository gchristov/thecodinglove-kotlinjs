package com.gchristov.thecodinglove.slack.event

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.BaseHttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.*
import com.gchristov.thecodinglove.slackdata.api.ApiSlackEvent
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackEvent
import com.gchristov.thecodinglove.slackdata.domain.toEvent
import com.gchristov.thecodinglove.slackdata.usecase.SlackRevokeTokensUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackVerifyRequestUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

class SlackEventHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
    log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val slackRevokeTokensUseCase: SlackRevokeTokensUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/slack/event",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = if (slackConfig.requestVerificationEnabled) {
        slackVerifyRequestUseCase(request)
    } else {
        Either.Right(Unit)
    }
        .flatMap {
            request.decodeBodyFromJson(
                jsonSerializer = jsonSerializer,
                strategy = ApiSlackEvent.serializer(),
            )
        }
        .leftIfNull { Exception("Request body is invalid") }
        .flatMap {
            when (val event = it.toEvent()) {
                is SlackEvent.UrlVerification -> event.handle(response)
                is SlackEvent.Callback -> event.handle(response)
            }
        }

    private fun SlackEvent.UrlVerification.handle(response: HttpResponse) = response.sendText(text = challenge)

    private suspend fun SlackEvent.Callback.handle(response: HttpResponse) =
        when (val typedEvent = event) {
            is SlackEvent.Callback.Event.TokensRevoked -> slackRevokeTokensUseCase(typedEvent).flatMap {
                response.sendEmpty()
            }
        }
}