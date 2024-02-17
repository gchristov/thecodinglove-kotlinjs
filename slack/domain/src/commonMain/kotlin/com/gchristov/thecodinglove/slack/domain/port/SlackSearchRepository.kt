package com.gchristov.thecodinglove.slack.domain.port

import arrow.core.Either

interface SlackSearchRepository {
    suspend fun search(query: String): Either<Throwable, SearchResultDto>

    suspend fun shuffle(searchSessionId: String): Either<Throwable, SearchResultDto>

    suspend fun deleteSearchSession(searchSessionId: String): Either<Throwable, Unit>

    suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SearchSessionStateDto,
    ): Either<Throwable, Unit>

    suspend fun getSearchSessionPost(searchSessionId: String): Either<Throwable, SearchSessionPostDto>

    data class SearchResultDto(
        val ok: Boolean,
        val error: Error?,
        val searchSession: SearchSession?,
    ) {
        sealed class Error {
            data object NoResults : Error()
        }

        data class SearchSession(
            val searchSessionId: String,
            val searchResults: Int,
            val post: SearchSessionPostDto,
        )
    }

    sealed class SearchSessionStateDto {
        data object Sent : SearchSessionStateDto()
        data object SelfDestruct : SearchSessionStateDto()
    }

    data class SearchSessionPostDto(
        val searchQuery: String,
        val attachmentTitle: String,
        val attachmentUrl: String,
        val attachmentImageUrl: String,
    )
}