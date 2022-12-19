package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.*
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.slack.usecase.VerifySlackRequestUseCase
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SlackSlashCommandApiService(
    private val jsonSerializer: Json,
    private val verifySlackRequestUseCase: VerifySlackRequestUseCase,
) : ApiService(jsonSerializer) {
    override fun register() {
        exports.slackSlashCommand = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ) {
        verifySlackRequestUseCase(request).fold(
            ifLeft = {
                sendError(
                    error = it,
                    response = response
                )
            },
            ifRight = {
                try {
                    // TODO: Handle valid request
                    val command: ApiSlackSlashCommand = request.bodyAsJson(jsonSerializer)
                    response.sendJson(data = jsonSerializer.encodeToString(command))
                } catch (error: Throwable) {
                    sendError(
                        error = error,
                        response = response
                    )
                }
            }
        )
    }
}