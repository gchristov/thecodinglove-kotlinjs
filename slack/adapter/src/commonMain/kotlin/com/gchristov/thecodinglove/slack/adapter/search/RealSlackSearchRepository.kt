package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchResult
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchSessionPost
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchResult
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackSearchSessionPost
import com.gchristov.thecodinglove.slack.adapter.search.model.ApiSlackUpdateSearchSessionState
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import io.ktor.client.call.*

internal class RealSlackSearchRepository(private val slackSearchServiceApi: SlackSearchServiceApi) :
    SlackSearchRepository {
    override suspend fun search(query: String) = try {
        val response: ApiSlackSearchResult = slackSearchServiceApi.search(query).body()
        Either.Right(response.toSearchResult())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun shuffle(searchSessionId: String) = try {
        val response: ApiSlackSearchResult = slackSearchServiceApi.shuffle(searchSessionId).body()
        Either.Right(response.toSearchResult())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during shuffle${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun deleteSearchSession(searchSessionId: String): Either<Throwable, Unit> = try {
        slackSearchServiceApi.deleteSearchSession(searchSessionId)
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during delete search session${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SlackSearchRepository.SearchSessionStateDto
    ) = try {
        slackSearchServiceApi.updateSearchSessionState(
            ApiSlackUpdateSearchSessionState(
                searchSessionId = searchSessionId,
                state = when (state) {
                    is SlackSearchRepository.SearchSessionStateDto.SelfDestruct -> ApiSlackUpdateSearchSessionState.ApiState.SelfDestruct
                    is SlackSearchRepository.SearchSessionStateDto.Sent -> ApiSlackUpdateSearchSessionState.ApiState.Sent
                },
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
        val response: ApiSlackSearchSessionPost = slackSearchServiceApi.getSearchSessionPost(searchSessionId).body()
        Either.Right(response.toSearchSessionPost())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search session post${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}