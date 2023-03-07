package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.model.Post
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchdata.model.SearchSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SearchUseCase {
    suspend operator fun invoke(type: Type): Either<SearchError, Result>

    sealed class Type {
        data class WithSessionId(val sessionId: String) : Type()
        data class NewSession(val query: String) : Type()
    }

    data class Result(
        val searchSessionId: String,
        val query: String,
        val post: Post,
        val totalPosts: Int
    )
}

class RealSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
) : SearchUseCase {
    override suspend operator fun invoke(
        type: SearchUseCase.Type
    ): Either<SearchError, SearchUseCase.Result> = withContext(dispatcher) {
        type
            .getSearchSession(searchRepository)
            .flatMap { searchSession ->
                val preloadedPost = searchSession.preloadedPost
                if (preloadedPost != null) {
                    // If a post is preloaded, use it right away
                    searchSession.usePreloadedPost(
                        preloadedPost = preloadedPost,
                        searchRepository = searchRepository
                    )
                        // TODO: Consider better error type
                        .mapLeft { SearchError.SessionNotFound }
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
                        query = searchSession.query,
                        totalPosts = searchSession.totalPosts,
                        searchHistory = searchSession.searchHistory,
                    ).fold(
                        ifLeft = { searchError ->
                            when (searchError) {
                                is SearchError.Exhausted -> {
                                    searchSession
                                        .clearExhaustedHistory(searchRepository)
                                        // TODO: Consider better error type
                                        .mapLeft { SearchError.SessionNotFound }
                                        .flatMap { invoke(type = type) }
                                }

                                else -> Either.Left(searchError)
                            }
                        },
                        ifRight = { searchResult ->
                            searchSession
                                .insertCurrentPost(
                                    searchResult = searchResult,
                                    searchRepository = searchRepository
                                )
                                // TODO: Consider better error type
                                .mapLeft { SearchError.SessionNotFound }
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
): Either<SearchError, SearchSession> = when (this) {
    is SearchUseCase.Type.NewSession -> Either.Right(
        SearchSession(
            id = uuid4().toString(),
            query = this.query,
            totalPosts = null,
            searchHistory = emptyMap(),
            currentPost = null,
            preloadedPost = null,
            state = SearchSession.State.Searching
        )
    )

    is SearchUseCase.Type.WithSessionId -> searchRepository
        .getSearchSession(this.sessionId)
        .mapLeft { SearchError.SessionNotFound }
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
    preloadedPost: Post,
    searchRepository: SearchRepository
) = searchRepository.saveSearchSession(
    copy(
        currentPost = preloadedPost,
        // Clear the preloaded post so that if it fails to load next time, we trigger search
        preloadedPost = null
    )
)