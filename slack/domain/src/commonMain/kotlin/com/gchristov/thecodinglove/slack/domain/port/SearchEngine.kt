package com.gchristov.thecodinglove.slack.domain.port

import arrow.core.Either

interface SearchEngine {
    suspend fun search(query: String): Either<Throwable, SearchEngineDto>
    suspend fun shuffle(searchSessionId: String): Either<Throwable, SearchEngineDto>
}

data class SearchEngineDto(
    val searchSessionId: String,
    val searchQuery: String,
    val searchResults: Int,
    val attachmentTitle: String,
    val attachmentUrl: String,
    val attachmentImageUrl: String,
)