package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.slackdata.SlackSlashCommandRepository

class SlackSlashCommandService(
    private val slackSlashCommandRepository: SlackSlashCommandRepository,
) {
    fun register() {
        exports.slackSlashCommand =
            slackSlashCommandRepository.observeSlashCommandRequest { request, response ->
                request.fold(
                    ifLeft = { error ->
                        error.printStackTrace()
                        slackSlashCommandRepository.sendSlashCommandErrorResponse(response)
                    },
                    ifRight = { command ->
                        // TODO: Actually handle this
                        slackSlashCommandRepository.sendSlashCommandResponse(
                            result = command,
                            response = response
                        )
                    }
                )
            }
    }
}