package com.gchristov.thecodinglove.common.monitoring.slack

import com.gchristov.thecodinglove.common.monitoring.slack.model.ApiMonitoringReportExceptionSlack
import com.gchristov.thecodinglove.common.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class MonitoringSlackServiceApi(
    private val client: NetworkClient.Json,
    private val apiUrl: String,
) {
    suspend fun reportException(
        exception: ApiMonitoringReportExceptionSlack
    ): HttpResponse = client.http.post("$apiUrl/slack/report-exception") {
        contentType(ContentType.Application.Json)
        setBody(exception)
    }
}