package com.gchristov.thecodinglove.slack.domain.ports

import arrow.core.Either

interface SearchSessionShuffle {
    suspend fun shuffle(searchSessionId: String): Either<Throwable, SearchSessionShuffleDto>
}

data class SearchSessionShuffleDto(
    val searchQuery: String,
    val searchResults: Int,
    val attachmentTitle: String,
    val attachmentUrl: String,
    val attachmentImageUrl: String,
)