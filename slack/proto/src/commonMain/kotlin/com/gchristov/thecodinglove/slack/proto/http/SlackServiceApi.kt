package com.gchristov.thecodinglove.slack.proto.http

import com.gchristov.thecodinglove.common.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SlackServiceApi(
    private val client: NetworkClient.Json,
    private val apiUrl: String,
) {
    suspend fun slackStatistics(): HttpResponse = client.http.get("$apiUrl/slack/statistics") {
        contentType(ContentType.Application.Json)
    }
}