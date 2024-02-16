package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.network.http.BaseHttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toAuthState
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackAuthState
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackAuthHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val slackAuthUseCase: SlackAuthUseCase,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log
) {
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
        return slackAuthUseCase(SlackAuthUseCase.Dto(code)).flatMap {
            val stateResult = state?.let { handleAuthState(it) } ?: Either.Right(Unit)
            stateResult.flatMap {
                response.redirect("/slack/auth/success")
                Either.Right(Unit)
            }
        }
    }

    override fun handleError(
        error: Throwable,
        response: HttpResponse,
    ): Either<Throwable, Unit> = when (error) {
        is SlackAuthUseCase.Error.Cancelled -> {
            response.redirect("/")
            Either.Right(Unit)
        }

        is SlackAuthUseCase.Error.Other -> {
            response.redirect("/slack/auth/error")
            Either.Right(Unit)
        }

        else -> super.handleError(error, response)
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