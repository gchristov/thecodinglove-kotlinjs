package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.domain.model.insert
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SearchUseCase {
    suspend operator fun invoke(dto: Dto): Either<Error, Result>

    sealed class Type {
        data class WithSessionId(val sessionId: String) : Type()
        data class NewSession(val query: String) : Type()
    }

    data class Result(
        val searchSessionId: String,
        val query: String,
        val post: SearchPost,
        val totalPosts: Int
    )

    data class Dto(val type: Type)

    sealed class Error(override val message: String? = null) : Throwable(message) {
        abstract val additionalInfo: String?

        data class Empty(
            override val additionalInfo: String? = null
        ) : Error("No results found${additionalInfo?.let { ": $it" } ?: ""}")

        data class SessionNotFound(
            override val additionalInfo: String? = null
        ) : Error("Session not found${additionalInfo?.let { ": $it" } ?: ""}")
    }
}

internal class RealSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
) : SearchUseCase {
    override suspend operator fun invoke(
        dto: SearchUseCase.Dto
    ): Either<SearchUseCase.Error, SearchUseCase.Result> = withContext(dispatcher) {
        dto.type
            .getSearchSession(searchRepository)
            .flatMap { searchSession ->
                val preloadedPost = searchSession.preloadedPost
                if (preloadedPost != null) {
                    // If a post is preloaded, use it right away
                    searchSession.usePreloadedPost(
                        preloadedPost = preloadedPost,
                        searchRepository = searchRepository
                    )
                        .mapLeft { SearchUseCase.Error.SessionNotFound(additionalInfo = it.message) }
                        .map {
                            SearchUseCase.Result(
                                searchSessionId = searchSession.id,
                                query = searchSession.query,
                                post = preloadedPost,
                                totalPosts = searchSession.totalPosts ?: 0
                            )
                        }
                } else {
                    // Else, run normal search
                    searchWithHistoryUseCase(
                        SearchWithHistoryUseCase.Dto(
                            query = searchSession.query,
                            totalPosts = searchSession.totalPosts,
                            searchHistory = searchSession.searchHistory,
                        )
                    ).fold(
                        ifLeft = { searchError ->
                            when (searchError) {
                                is SearchWithHistoryUseCase.Error.Exhausted -> {
                                    searchSession
                                        .clearExhaustedHistory(searchRepository)
                                        .mapLeft { SearchUseCase.Error.SessionNotFound(additionalInfo = it.message) }
                                        .flatMap { invoke(dto) }
                                }

                                is SearchWithHistoryUseCase.Error.Empty -> Either.Left(
                                    SearchUseCase.Error.Empty(additionalInfo = searchError.additionalInfo)
                                )
                            }
                        },
                        ifRight = { searchResult ->
                            searchSession
                                .insertCurrentPost(
                                    searchResult = searchResult,
                                    searchRepository = searchRepository
                                )
                                .mapLeft { SearchUseCase.Error.SessionNotFound(additionalInfo = it.message) }
                                .map {
                                    SearchUseCase.Result(
                                        searchSessionId = searchSession.id,
                                        query = searchResult.query,
                                        post = searchResult.post,
                                        totalPosts = searchResult.totalPosts
                                    )
                                }
                        }
                    )
                }
            }
    }
}

private suspend fun SearchUseCase.Type.getSearchSession(
    searchRepository: SearchRepository
): Either<SearchUseCase.Error, SearchSession> = when (this) {
    is SearchUseCase.Type.NewSession -> Either.Right(
        SearchSession(
            id = uuid4().toString(),
            query = this.query,
            totalPosts = null,
            searchHistory = emptyMap(),
            currentPost = null,
            preloadedPost = null,
            state = SearchSession.State.Searching()
        )
    )

    is SearchUseCase.Type.WithSessionId -> searchRepository
        .getSearchSession(this.sessionId)
        .mapLeft { SearchUseCase.Error.SessionNotFound(additionalInfo = it.message) }
}

private suspend fun SearchSession.insertCurrentPost(
    searchResult: SearchWithHistoryUseCase.Result,
    searchRepository: SearchRepository
) = searchRepository.saveSearchSession(
    copy(
        totalPosts = searchResult.totalPosts,
        searchHistory = searchHistory.toMutableMap().apply {
            insert(
                postPage = searchResult.postPage,
                postIndexOnPage = searchResult.postIndexOnPage,
                currentPageSize = searchResult.postPageSize
            )
        },
        currentPost = searchResult.post
    )
)

private suspend fun SearchSession.clearExhaustedHistory(
    searchRepository: SearchRepository
) = searchRepository.saveSearchSession(
    copy(
        searchHistory = emptyMap(),
        currentPost = null,
    )
)

private suspend fun SearchSession.usePreloadedPost(
    preloadedPost: SearchPost,
    searchRepository: SearchRepository
) = searchRepository.saveSearchSession(
    copy(
        currentPost = preloadedPost,
        // Clear the preloaded post so that if it fails to load next time, we trigger search
        preloadedPost = null
    )
)