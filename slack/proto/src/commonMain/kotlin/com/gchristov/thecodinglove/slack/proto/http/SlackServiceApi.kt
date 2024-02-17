package com.gchristov.thecodinglove.slack.proto.http

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackReportException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/*
 * The Slack service handles a few additional HTTP calls (events, interactivity, slash commands) but since they are
 * called by Slack directly we do not expose them as part of the public service APIs.
 */
internal class SlackServiceApi(
    private val client: NetworkClient.Json,
    private val apiUrl: String,
) {
    suspend fun slackStatistics(): HttpResponse = client.http.get("$apiUrl/slack/statistics") {
        contentType(ContentType.Application.Json)
    }

    suspend fun selfDestruct(): HttpResponse = client.http.get("$apiUrl/slack/self-destruct") {
        contentType(ContentType.Application.Json)
    }

    suspend fun reportException(
        exception: ApiSlackReportException
    ): HttpResponse = client.http.post("$apiUrl/slack/report-exception") {
        contentType(ContentType.Application.Json)
        setBody(exception)
    }
}