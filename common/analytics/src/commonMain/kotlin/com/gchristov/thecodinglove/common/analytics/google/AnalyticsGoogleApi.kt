package com.gchristov.thecodinglove.common.analytics.google

import com.gchristov.thecodinglove.common.analytics.google.model.ApiAnalyticsGoogleRequest
import com.gchristov.thecodinglove.common.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class AnalyticsGoogleApi(
    private val client: NetworkClient.Json,
    private val measurementId: String,
    private val apiSecret: String,
) {
    suspend fun sendEvent(request: ApiAnalyticsGoogleRequest): HttpResponse =
        client.http.post("${Domain}?measurement_id=$measurementId&api_secret=$apiSecret") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
}

private const val Domain = "https://www.google-analytics.com/mp/collect"