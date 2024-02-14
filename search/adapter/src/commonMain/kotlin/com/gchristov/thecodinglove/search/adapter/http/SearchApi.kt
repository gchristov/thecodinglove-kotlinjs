package com.gchristov.thecodinglove.search.adapter.http

import com.gchristov.thecodinglove.common.network.NetworkClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SearchApi(private val client: NetworkClient.Html) {
    suspend fun search(
        page: Int,
        query: String
    ): HttpResponse = client.http.get("${Domain}/page/$page?s=$query") {
        accept(ContentType.Text.Html)
    }
}

private const val Domain = "https://thecodinglove.com"