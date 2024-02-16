package com.gchristov.thecodinglove.search.proto.http

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.search.proto.http.model.ApiUpdateSearchSessionState
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SearchServiceApi(
    private val client: NetworkClient.Json,
    private val apiUrl: String,
) {
    suspend fun deleteSearchSession(
        searchSessionId: String,
    ): HttpResponse = client.http.delete("$apiUrl/search/session") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchSessionId", searchSessionId)
        }
    }

    suspend fun getSearchSessionPost(
        searchSessionId: String,
    ): HttpResponse = client.http.get("$apiUrl/search/session-post") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchSessionId", searchSessionId)
        }
    }

    suspend fun updateSearchSessionState(
        updateSearchSessionState: ApiUpdateSearchSessionState,
    ): HttpResponse = client.http.put("$apiUrl/search/session-state") {
        contentType(ContentType.Application.Json)
        setBody(updateSearchSessionState)
    }

    suspend fun search(
        query: String,
    ): HttpResponse = client.http.get("$apiUrl/search") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchQuery", query)
        }
    }

    suspend fun shuffle(
        searchSessionId: String,
    ): HttpResponse = client.http.get("$apiUrl/search") {
        contentType(ContentType.Application.Json)
        url {
            parameters.append("searchSessionId", searchSessionId)
        }
    }

    suspend fun searchStatistics(): HttpResponse = client.http.get("$apiUrl/search/statistics") {
        contentType(ContentType.Application.Json)
    }
}