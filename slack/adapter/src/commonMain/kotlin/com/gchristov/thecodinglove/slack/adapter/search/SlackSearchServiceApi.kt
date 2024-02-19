package com.gchristov.thecodinglove.slack.adapter.search

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackUpdateSearchSessionState
import com.gchristov.thecodinglove.slack.domain.model.Environment
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SlackSearchServiceApi(
    private val client: NetworkClient.Json,
    private val environment: Environment,
) {
    suspend fun deleteSearchSession(
        searchSessionId: String,
    ): HttpResponse = client.http.delete("${environment.apiUrl}/search/session") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchSessionId", searchSessionId)
        }
    }

    suspend fun getSearchSessionPost(
        searchSessionId: String,
    ): HttpResponse = client.http.get("${environment.apiUrl}/search/session-post") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchSessionId", searchSessionId)
        }
    }

    suspend fun updateSearchSessionState(
        updateSearchSessionState: ApiSlackUpdateSearchSessionState,
    ): HttpResponse = client.http.put("${environment.apiUrl}/search/session-state") {
        contentType(ContentType.Application.Json)
        setBody(updateSearchSessionState)
    }

    suspend fun search(
        query: String,
    ): HttpResponse = client.http.get("${environment.apiUrl}/search") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchQuery", query)
        }
    }

    suspend fun shuffle(
        searchSessionId: String,
    ): HttpResponse = client.http.get("${environment.apiUrl}/search") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchSessionId", searchSessionId)
        }
    }
}