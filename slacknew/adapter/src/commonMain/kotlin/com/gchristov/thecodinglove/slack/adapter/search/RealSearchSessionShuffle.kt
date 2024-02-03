package com.gchristov.thecodinglove.slack.adapter.search

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slack.domain.ports.SearchSessionShuffle
import com.gchristov.thecodinglove.slack.domain.ports.SearchSessionShuffleDto

class RealSearchSessionShuffle(private val searchUseCase: SearchUseCase) : SearchSessionShuffle {
    override suspend fun shuffle(searchSessionId: String) =
        searchUseCase(SearchUseCase.Type.WithSessionId(searchSessionId))
            .flatMap {
                Either.Right(
                    SearchSessionShuffleDto(
                        searchQuery = it.query,
                        searchResults = it.totalPosts,
                        attachmentTitle = it.post.title,
                        attachmentUrl = it.post.url,
                        attachmentImageUrl = it.post.imageUrl,
                    )
                )
            }
}