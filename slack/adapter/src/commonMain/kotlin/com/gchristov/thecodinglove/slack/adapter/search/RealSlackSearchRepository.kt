package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.search.proto.http.SearchServiceRepository
import com.gchristov.thecodinglove.search.proto.http.model.ApiUpdateSearchSessionState
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchResult
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchSessionPost
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository

internal class RealSlackSearchRepository(private val searchServiceRepository: SearchServiceRepository) : SlackSearchRepository {
    override suspend fun search(query: String) = searchServiceRepository.search(query).map { it.toSearchResult() }

    override suspend fun shuffle(searchSessionId: String) =
        searchServiceRepository.shuffle(searchSessionId).map { it.toSearchResult() }

    override suspend fun deleteSearchSession(searchSessionId: String): Either<Throwable, Unit> =
        searchServiceRepository.deleteSearchSession(searchSessionId)

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SlackSearchRepository.SearchSessionStateDto
    ): Either<Throwable, Unit> = searchServiceRepository.updateSearchSessionState(
        searchSessionId = searchSessionId,
        state = ApiUpdateSearchSessionState(
            searchSessionId = searchSessionId,
            state = when (state) {
                is SlackSearchRepository.SearchSessionStateDto.SelfDestruct -> ApiUpdateSearchSessionState.ApiState.SelfDestruct
                is SlackSearchRepository.SearchSessionStateDto.Sent -> ApiUpdateSearchSessionState.ApiState.Sent
            }
        )
    )

    override suspend fun getSearchSessionPost(searchSessionId: String): Either<Throwable, SlackSearchRepository.SearchSessionPostDto> =
        searchServiceRepository.getSearchSessionPost(searchSessionId).map { it.toSearchSessionPost() }
}