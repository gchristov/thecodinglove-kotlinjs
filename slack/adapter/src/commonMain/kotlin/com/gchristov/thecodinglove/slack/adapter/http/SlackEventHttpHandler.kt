package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.raise.either
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
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val slackRevokeTokensUseCase: SlackRevokeTokensUseCase,
    private val analytics: Analytics,
) : HttpHandler {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/slack/event",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        if (slackConfig.requestVerificationEnabled) {
            val verifyDto = request.toSlackRequestVerificationDto().bind()
            slackVerifyRequestUseCase(verifyDto).bind()
        }
        val apiEvent = request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = ApiSlackEvent.serializer(),
        ).bind() ?: raise(Exception("Request body is invalid"))
        when (val event = apiEvent.toEvent()) {
            is SlackEvent.UrlVerification -> event.handle(response).bind()
            is SlackEvent.Callback -> event.handle(response).bind()
        }
    }

    private fun SlackEvent.UrlVerification.handle(response: HttpResponse) = response.sendText(text = challenge)

    private suspend fun SlackEvent.Callback.handle(response: HttpResponse) =
        when (val typedEvent = event) {
            is SlackEvent.Callback.Event.TokensRevoked -> either {
                val revokeTokensDto = typedEvent.toSlackRevokeTokensDto()
                slackRevokeTokensUseCase(revokeTokensDto).bind()
                val params = mutableMapOf<String, String>().apply {
                    revokeTokensDto.bot?.forEach { put(it, true.toString()) }
                    revokeTokensDto.oAuth?.forEach { put(it, true.toString()) }
                }
                analytics.sendEvent(
                    clientId = uuid4().toString(),
                    name = "app_revoke_tokens",
                    params = params,
                )
                response.sendEmpty().bind()
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
