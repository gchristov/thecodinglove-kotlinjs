package com.gchristov.thecodinglove.selfdestruct.adapter.slack

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.selfdestruct.domain.model.Environment
import io.ktor.client.request.*
import io.ktor.client.statement.*

internal class SlackSelfDestructApi(
    private val client: NetworkClient.Json,
    private val environment: Environment,
) {
    suspend fun selfDestruct(): HttpResponse = client.http.get("${environment.apiUrl}/slack/self-destruct")
}