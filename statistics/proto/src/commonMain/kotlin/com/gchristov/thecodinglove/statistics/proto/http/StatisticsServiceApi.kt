package com.gchristov.thecodinglove.statistics.proto.http

import com.gchristov.thecodinglove.common.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class StatisticsServiceApi(
    private val client: NetworkClient.Json,
    private val apiUrl: String,
) {
    suspend fun selfDestruct(): HttpResponse = client.http.get("$apiUrl/statistics") {
        contentType(ContentType.Application.Json)
    }
}