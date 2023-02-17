package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.sendJson
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import kotlinx.serialization.json.Json

interface SlackRepository {
    fun sendProcessingMessage(
        text: String,
        response: ApiResponse
    )
}

internal class RealSlackRepository(private val jsonSerializer: Json) : SlackRepository {
    override fun sendProcessingMessage(
        text: String,
        response: ApiResponse
    ) {
        response.sendJson(
            data = ApiSlackMessage.ApiProcessing(text = text),
            jsonSerializer = jsonSerializer
        )
    }
}