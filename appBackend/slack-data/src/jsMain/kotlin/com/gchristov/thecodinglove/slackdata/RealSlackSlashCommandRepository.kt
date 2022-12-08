package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctions
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctionsResponse
import com.gchristov.thecodinglove.commonfirebase.bodyFromJson
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.toSlashCommand
import kotlinx.serialization.json.Json

internal class RealSlackSlashCommandRepository(
    private val jsonParser: Json
) : SlackSlashCommandRepository {
    override fun observeSlashCommand(callback: (SlackSlashCommand?, FirebaseFunctionsResponse) -> Unit) =
        FirebaseFunctions.https.onRequest { request, response ->
            val test: ApiSlackSlashCommand? = request.body.bodyFromJson(jsonParser)
            callback(test?.toSlashCommand(), response)
        }
}