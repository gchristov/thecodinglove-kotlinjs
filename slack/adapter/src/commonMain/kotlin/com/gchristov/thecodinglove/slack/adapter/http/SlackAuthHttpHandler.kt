package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toAuthState
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthState
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackAuthHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val slackAuthUseCase: SlackAuthUseCase,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val analytics: Analytics,
) : HttpHandler {
    private val tag = this::class.simpleName

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/slack/auth",
        contentType = ContentType.Application.FormUrlEncoded,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        val code: String? = request.query["code"]
        val state = request.query.get<String?>("state").takeIf { !it.isNullOrEmpty() }
        return either {
            slackAuthUseCase(SlackAuthUseCase.Dto(code)).bind()
            state?.let { handleAuthState(it).bind() }
            analytics.sendEvent(
                clientId = uuid4().toString(),
                name = "slack_auth_success",
            )
            response.redirect("/slack/auth/success").bind()
        }
    }

    override suspend fun handleError(
        error: Throwable,
        response: HttpResponse,
    ): Either<Throwable, Unit> = when (error) {
        is SlackAuthUseCase.Error.Cancelled -> {
            analytics.sendEvent(
                clientId = uuid4().toString(),
                name = "slack_auth_cancel",
            )
            response.redirect("/")
        }

        is SlackAuthUseCase.Error.Other -> {
            analytics.sendEvent(
                clientId = uuid4().toString(),
                name = "slack_auth_error",
                params = error.message?.let { mapOf("info" to it) }
            )
            response.redirect("/slack/auth/error")
        }

        else -> super<HttpHandler>.handleError(error, response)
    }

    private suspend fun handleAuthState(state: String) = try {
        val base64Decoded = state.decodeBase64String()
        log.debug(tag, "Decoded Base64 auth state: decoded=$base64Decoded")
        val authState = jsonSerializer.json
            .decodeFromString<ApiSlackAuthState>(base64Decoded)
            .toAuthState()
        log.debug(tag, "Parsed Base64 auth state: parsed=$authState")
        slackSendSearchUseCase.invoke(
            SlackSendSearchUseCase.Dto(
                userId = authState.userId,
                teamId = authState.teamId,
                channelId = authState.channelId,
                responseUrl = authState.responseUrl,
                searchSessionId = authState.searchSessionId,
                selfDestructMinutes = authState.selfDestructMinutes,
            )
        )
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error handling auth state${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}
