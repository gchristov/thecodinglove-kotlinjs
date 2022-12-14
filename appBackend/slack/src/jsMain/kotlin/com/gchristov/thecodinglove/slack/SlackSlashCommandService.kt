package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservice.*
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SlackSlashCommandService(
    private val jsonParser: Json
) : Service() {
    override fun register() {
        exports.slackSlashCommand = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ) {
        try {
            // TODO: Needs correct response mapping
            val command: ApiSlackSlashCommand = request.body.bodyFromJson(jsonParser)
            response.send(Json.encodeToString(command))
        } catch (error: Exception) {
            error.printStackTrace()
            // TODO: Needs correct response mapping
            response.status(400).send("ERROR")
        }
    }
}