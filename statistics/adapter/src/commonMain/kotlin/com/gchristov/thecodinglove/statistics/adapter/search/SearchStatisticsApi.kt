package com.gchristov.thecodinglove.statistics.adapter.search

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.statistics.domain.model.Environment
import io.ktor.client.request.*
import io.ktor.client.statement.*

internal class SearchStatisticsApi(
    private val client: NetworkClient.Json,
    private val environment: Environment,
) {
    suspend fun statistics(): HttpResponse = client.http.get("${environment.apiUrl}/search/statistics")
}