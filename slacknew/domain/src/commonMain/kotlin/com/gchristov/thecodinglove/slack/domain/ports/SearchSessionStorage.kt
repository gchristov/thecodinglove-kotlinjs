package com.gchristov.thecodinglove.slack.domain.ports

import arrow.core.Either

interface SearchSessionStorage {
    suspend fun deleteSearchSession(id: String): Either<Throwable, Unit>

    suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SearchSessionState,
    ): Either<Throwable, Unit>

    suspend fun getSearchSessionPost(searchSessionId: String): Either<Throwable, SearchSessionPost>
}

sealed class SearchSessionState {
    data object Sent : SearchSessionState()
    data object SelfDestruct : SearchSessionState()
}

data class SearchSessionPost(
    val searchQuery: String,
    val attachmentTitle: String,
    val attachmentUrl: String,
    val attachmentImageUrl: String,
)