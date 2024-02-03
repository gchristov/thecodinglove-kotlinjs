package com.gchristov.thecodinglove.slack.domain.ports

import arrow.core.Either

interface SearchSessionStorage {
    suspend fun deleteSearchSession(searchSessionId: String): Either<Throwable, Unit>

    suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SearchSessionStateDto,
    ): Either<Throwable, Unit>

    suspend fun getSearchSessionPost(searchSessionId: String): Either<Throwable, SearchSessionPostDto>
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