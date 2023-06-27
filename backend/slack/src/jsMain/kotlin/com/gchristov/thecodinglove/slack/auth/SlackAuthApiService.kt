package com.gchristov.thecodinglove.slack.auth

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthState
import com.gchristov.thecodinglove.slackdata.domain.toAuthState
import com.gchristov.thecodinglove.slackdata.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slackdata.usecase.SlackSendSearchUseCase
import io.ktor.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SlackAuthApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val slackAuthUseCase: SlackAuthUseCase,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun register() {
        exports.slackAuthApi = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
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
        response: ApiResponse
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
        val authState = jsonSerializer
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
        log.e(error) { error.message ?: "Error during auth state handling" }
        Either.Left(error)
    }
}