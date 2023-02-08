package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.*
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.slack.usecase.VerifySlackRequestUseCase
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.toSlashCommand
import kotlinx.serialization.json.Json

class SlackSlashCommandApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val verifySlackRequestUseCase: VerifySlackRequestUseCase,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer
) {
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
                    val apiCommand: ApiSlackSlashCommand =
                        requireNotNull(request.bodyAsJson(jsonSerializer))
                    val command = apiCommand.toSlashCommand()
                    println(command)
                    response.sendJson(
                        data = apiCommand,
                        jsonSerializer = jsonSerializer
                    )
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