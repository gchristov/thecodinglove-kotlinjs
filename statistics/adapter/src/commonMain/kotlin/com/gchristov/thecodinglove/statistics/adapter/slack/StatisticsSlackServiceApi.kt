package com.gchristov.thecodinglove.statistics.adapter.slack

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.statistics.domain.model.Environment
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class StatisticsSlackServiceApi(
    private val client: NetworkClient.Json,
    private val environment: Environment,
) {
    suspend fun statistics(): HttpResponse = client.http.get("${environment.apiUrl}/slack/statistics") {
        contentType(ContentType.Application.Json)
    }
}