package com.gchristov.thecodinglove.common.monitoring.slack

import com.gchristov.thecodinglove.common.monitoring.slack.model.ApiMonitoringSlackMessage
import com.gchristov.thecodinglove.common.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class MonitoringSlackApi(
    private val client: NetworkClient.Json,
    private val monitoringSlackUrl: String,
) {
    suspend fun reportException(message: ApiMonitoringSlackMessage): HttpResponse =
        client.http.post(monitoringSlackUrl) {
            contentType(ContentType.Application.Json)
            setBody(message)
        }
}