package com.gchristov.thecodinglove.slack.auth

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.sendEmpty
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.slackdata.usecase.SlackAuthUserUseCase
import kotlinx.serialization.json.Json

class SlackAuthApiService(
    apiServiceRegister: ApiServiceRegister,
    jsonSerializer: Json,
    private val log: Logger,
    private val slackAuthUserUseCase: SlackAuthUserUseCase,
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
        return slackAuthUserUseCase(
            code = code,
            searchSessionId = searchSessionId,
        )
            .mapLeft {
                // TODO: Redirect to error
                it
            }
            .flatMap {
                // TODO: Redirect to success
                response.sendEmpty(log = log)
            }
    }
}