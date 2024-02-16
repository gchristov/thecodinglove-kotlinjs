package com.gchristov.thecodinglove.selfdestruct.proto.http

import com.gchristov.thecodinglove.common.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SelfDestructServiceApi(
    private val client: NetworkClient.Json,
    private val apiUrl: String,
) {
    suspend fun selfDestruct(): HttpResponse = client.http.get("$apiUrl/self-destruct") {
        contentType(ContentType.Application.Json)
    }
}