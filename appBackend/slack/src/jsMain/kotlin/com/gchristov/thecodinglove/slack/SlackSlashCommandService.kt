package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservice.Service
import com.gchristov.thecodinglove.commonservice.bodyFromJson
import com.gchristov.thecodinglove.commonservice.exports
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SlackSlashCommandService(
    private val jsonParser: Json
) : Service() {
    override fun register() {
        exports.slackSlashCommand = registerApiCallback { request, response ->
            try {
                val command: ApiSlackSlashCommand = request.body.bodyFromJson(jsonParser)
                response.send(Json.encodeToString(command))
            } catch (error: Exception) {
                response.send("ERROR")
            }
        }
    }
}