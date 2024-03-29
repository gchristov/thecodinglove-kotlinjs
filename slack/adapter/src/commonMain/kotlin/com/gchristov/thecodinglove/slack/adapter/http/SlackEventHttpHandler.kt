package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Logger.Companion.tag
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toEvent
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toSlackRequestVerificationDto
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toSlackRevokeTokensDto
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.model.SlackEvent
import com.gchristov.thecodinglove.slack.domain.usecase.SlackRevokeTokensUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackEventHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val slackRevokeTokensUseCase: SlackRevokeTokensUseCase,
    private val analytics: Analytics,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
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
        request
            .toSlackRequestVerificationDto()
            .flatMap { slackVerifyRequestUseCase(it) }
    } else {
        Either.Right(Unit)
    }
        .flatMap {
            request.decodeBodyFromJson(
                jsonSerializer = jsonSerializer,
                strategy = ApiSlackEvent.serializer(),
            )
        }
        .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
        .flatMap {
            when (val event = it.toEvent()) {
                is SlackEvent.UrlVerification -> event.handle(response)
                is SlackEvent.Callback -> event.handle(response)
            }
        }

    private fun SlackEvent.UrlVerification.handle(response: HttpResponse) = response.sendText(text = challenge)

    private suspend fun SlackEvent.Callback.handle(response: HttpResponse) =
        when (val typedEvent = event) {
            is SlackEvent.Callback.Event.TokensRevoked -> {
                val revokeTokensDto = typedEvent.toSlackRevokeTokensDto()
                slackRevokeTokensUseCase(revokeTokensDto).flatMap {
                    val params = mutableMapOf<String, String>().apply {
                        revokeTokensDto.bot?.forEach { put(it, true.toString()) }
                        revokeTokensDto.oAuth?.forEach { put(it, true.toString()) }
                    }
                    analytics.sendEvent(
                        clientId = uuid4().toString(),
                        name = "app_revoke_tokens",
                        params = params,
                    )
                    response.sendEmpty()
                }
            }

            is SlackEvent.Callback.Event.AppUninstalled -> {
                log.debug(tag, "App uninstalled: teamId=$teamId")
                analytics.sendEvent(
                    clientId = uuid4().toString(),
                    name = "app_uninstall",
                    params = mapOf("team_id" to teamId),
                )
                response.sendEmpty()
            }
        }
}