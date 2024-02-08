package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import com.gchristov.thecodinglove.slack.domain.port.SearchSessionPostDto
import com.gchristov.thecodinglove.slack.domain.port.SearchSessionStateDto
import com.gchristov.thecodinglove.slack.domain.port.SearchSessionStorage

class RealSearchSessionStorage(private val searchRepository: SearchRepository) : SearchSessionStorage {
    override suspend fun deleteSearchSession(searchSessionId: String) =
        searchRepository.deleteSearchSession(searchSessionId)

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SearchSessionStateDto
    ): Either<Throwable, Unit> = searchRepository
        .getSearchSession(searchSessionId)
        .flatMap {
            searchRepository.saveSearchSession(
                it.copy(
                    state = when (state) {
                        SearchSessionStateDto.SelfDestruct -> SearchSession.State.SelfDestruct()
                        SearchSessionStateDto.Sent -> SearchSession.State.Sent()
                    }
                )
            )
        }

    override suspend fun getSearchSessionPost(searchSessionId: String) =
        searchRepository
            .getSearchSession(searchSessionId)
            .flatMap {
                Either.Right(
                    SearchSessionPostDto(
                        searchQuery = it.query,
                        attachmentTitle = it.currentPost!!.title,
                        attachmentUrl = it.currentPost!!.url,
                        attachmentImageUrl = it.currentPost!!.imageUrl,
                    )
                )
            }
}