package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchSessionPost
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSearchSessionPost
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiUpdateSearchSessionState
import com.gchristov.thecodinglove.slack.domain.port.SearchSessionStateDto
import com.gchristov.thecodinglove.slack.domain.port.SearchSessionStorage
import io.ktor.client.call.*

internal class RealSearchSessionStorage(private val apiService: SearchApi) : SearchSessionStorage {
    override suspend fun deleteSearchSession(searchSessionId: String) = try {
        apiService.deleteSearchSession(searchSessionId)
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during delete search session${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SearchSessionStateDto
    ): Either<Throwable, Unit> = try {
        apiService.updateSearchSessionState(
            ApiUpdateSearchSessionState(
                searchSessionId = searchSessionId,
                state = when (state) {
                    is SearchSessionStateDto.SelfDestruct -> ApiUpdateSearchSessionState.ApiState.SelfDestruct
                    is SearchSessionStateDto.Sent -> ApiUpdateSearchSessionState.ApiState.Sent
                }
            )
        )
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during update search session state${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun getSearchSessionPost(searchSessionId: String) = try {
        val response: ApiSearchSessionPost = apiService.getSearchSessionPost(searchSessionId).body()
        Either.Right(response.toSearchSessionPost())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search session post${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}