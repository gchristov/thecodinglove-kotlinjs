package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservice.Service
import com.gchristov.thecodinglove.commonservice.exports
import kotlinx.serialization.json.Json

class SlackSlashCommandService(jsonParser: Json) : Service(jsonParser) {
    override fun register() {
        exports.slackSlashCommand = registerApiCallback { request, response ->
            response.send("SLASH")
        }
    }
}