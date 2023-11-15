package com.gchristov.thecodinglove.slack

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.BaseHttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthState
import com.gchristov.thecodinglove.slackdata.domain.toAuthState
import com.gchristov.thecodinglove.slackdata.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackSendSearchUseCase
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.decodeFromString

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
        return slackAuthUseCase(code = code).flatMap {
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
        log.d("Decoded Base64 auth state: decoded=$base64Decoded")
        val authState = jsonSerializer.json
            .decodeFromString<ApiSlackAuthState>(base64Decoded)
            .toAuthState()
        log.d("Parsed Base64 auth state: parsed=$authState")
        slackSendSearchUseCase.invoke(
            userId = authState.userId,
            teamId = authState.teamId,
            channelId = authState.channelId,
            responseUrl = authState.responseUrl,
            searchSessionId = authState.searchSessionId,
        )
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error handling auth state${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}