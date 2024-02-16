package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.search.proto.http.SearchApiRepository
import com.gchristov.thecodinglove.search.proto.http.model.ApiUpdateSearchSessionState
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchResult
import com.gchristov.thecodinglove.slack.adapter.search.mapper.toSearchSessionPost
import com.gchristov.thecodinglove.slack.domain.port.SearchRepository

internal class RealSearchRepository(private val searchApiRepository: SearchApiRepository) : SearchRepository {
    override suspend fun search(query: String) = searchApiRepository.search(query).map { it.toSearchResult() }

    override suspend fun shuffle(searchSessionId: String) =
        searchApiRepository.shuffle(searchSessionId).map { it.toSearchResult() }

    override suspend fun deleteSearchSession(searchSessionId: String): Either<Throwable, Unit> =
        searchApiRepository.deleteSearchSession(searchSessionId)

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SearchRepository.SearchSessionStateDto
    ): Either<Throwable, Unit> = searchApiRepository.updateSearchSessionState(
        searchSessionId = searchSessionId,
        state = ApiUpdateSearchSessionState(
            searchSessionId = searchSessionId,
            state = when (state) {
                is SearchRepository.SearchSessionStateDto.SelfDestruct -> ApiUpdateSearchSessionState.ApiState.SelfDestruct
                is SearchRepository.SearchSessionStateDto.Sent -> ApiUpdateSearchSessionState.ApiState.Sent
            }
        )
    )

    override suspend fun getSearchSessionPost(searchSessionId: String): Either<Throwable, SearchRepository.SearchSessionPostDto> =
        searchApiRepository.getSearchSessionPost(searchSessionId).map { it.toSearchSessionPost() }
}