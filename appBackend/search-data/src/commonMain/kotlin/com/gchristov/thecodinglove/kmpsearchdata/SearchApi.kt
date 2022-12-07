package com.gchristov.thecodinglove.kmpsearchdata

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

internal class SearchApi(private val client: HttpClient) {
    suspend fun search(
        page: Int,
        query: String
    ): HttpResponse = client.get("$Domain/page/$page?s=$query")
}

private const val Domain = "https://thecodinglove.com"