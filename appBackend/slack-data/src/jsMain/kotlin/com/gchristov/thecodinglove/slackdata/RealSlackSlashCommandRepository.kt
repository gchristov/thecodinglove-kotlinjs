package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctions
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctionsResponse
import com.gchristov.thecodinglove.commonfirebase.bodyFromJson
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.api.toSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.toSlashCommand
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class RealSlackSlashCommandRepository(
    private val jsonParser: Json
) : SlackSlashCommandRepository {
    override fun observeSlashCommandRequest(
        callback: (
            request: Either<Exception, SlackSlashCommand>,
            response: FirebaseFunctionsResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        try {
            val command = request.body
                .bodyFromJson<ApiSlackSlashCommand>(jsonParser)
                .toSlashCommand()
            callback(Either.Right(command), response)
        } catch (error: Exception) {
            callback(Either.Left(error), response)
        }
    }

    override fun sendSlashCommandResponse(
        result: SlackSlashCommand,
        response: FirebaseFunctionsResponse
    ) {
        // TODO: Needs correct mapping
        val jsonResponse = Json.encodeToString(result.toSlashCommand())
        response.send(jsonResponse)
    }

    override fun sendSlashCommandErrorResponse(response: FirebaseFunctionsResponse) {
        // TODO: Needs correct mapping
        response.send("{}")
    }
}