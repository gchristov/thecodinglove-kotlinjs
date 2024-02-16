package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.search.proto.http.ApiSearchResult
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchResult
import com.gchristov.thecodinglove.slack.domain.port.SearchEngine
import io.ktor.client.call.*

internal class RealSearchEngine(private val searchApi: SearchApi) : SearchEngine {
    override suspend fun search(query: String) = try {
        val response: ApiSearchResult = searchApi.search(query).body()
        Either.Right(response.toSearchResult())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun shuffle(searchSessionId: String) = try {
        val response: ApiSearchResult = searchApi.shuffle(searchSessionId).body()
        Either.Right(response.toSearchResult())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during shuffle${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}