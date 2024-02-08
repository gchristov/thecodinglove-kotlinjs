package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slack.domain.port.SearchEngine
import com.gchristov.thecodinglove.slack.domain.port.SearchEngineDto

class RealSearchEngine(private val searchUseCase: SearchUseCase) : SearchEngine {
    override suspend fun search(query: String): Either<Throwable, SearchEngineDto> =
        search(SearchUseCase.Type.NewSession(query))

    override suspend fun shuffle(searchSessionId: String) = search(SearchUseCase.Type.WithSessionId(searchSessionId))

    private suspend fun search(type: SearchUseCase.Type) = searchUseCase(type).flatMap {
        Either.Right(
            SearchEngineDto(
                searchSessionId = it.searchSessionId,
                searchQuery = it.query,
                searchResults = it.totalPosts,
                attachmentTitle = it.post.title,
                attachmentUrl = it.post.url,
                attachmentImageUrl = it.post.imageUrl,
            )
        )
    }
}