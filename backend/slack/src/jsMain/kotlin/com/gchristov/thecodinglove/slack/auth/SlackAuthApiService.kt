package com.gchristov.thecodinglove.slack.auth

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.slackdata.usecase.SlackAuthUseCase
import kotlinx.serialization.json.Json

class SlackAuthApiService(
    apiServiceRegister: ApiServiceRegister,
    jsonSerializer: Json,
    log: Logger,
    private val slackAuthUseCase: SlackAuthUseCase,
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
        val searchSessionId: String? = request.query["state"]
        return slackAuthUseCase(
            code = code,
            searchSessionId = searchSessionId,
        ).flatMap {
            response.redirect("/slack/auth/success")
            Either.Right(Unit)
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
}