package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctionsResponse
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommand

interface SlackSlashCommandRepository {
    fun observeSlashCommandRequest(
        callback: (
            request: Either<Exception, SlackSlashCommand>,
            response: FirebaseFunctionsResponse
        ) -> Unit
    )

    fun sendSlashCommandResponse(
        result: SlackSlashCommand,
        response: FirebaseFunctionsResponse
    )

    fun sendSlashCommandErrorResponse(response: FirebaseFunctionsResponse)
}