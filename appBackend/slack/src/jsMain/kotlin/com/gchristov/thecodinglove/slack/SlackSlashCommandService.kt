package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservice.Service
import com.gchristov.thecodinglove.commonservice.exports

class SlackSlashCommandService : Service() {
    override fun register() {
        exports.slackSlashCommand = registerApiCallback { request, response ->
            response.send("SLASH")
        }
    }
}