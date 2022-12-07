package com.gchristov.thecodinglove.kmpsearch.usecase

import arrow.core.Either
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.kmpsearch.insert
import com.gchristov.thecodinglove.kmpsearchdata.SearchException
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class RealSearchWithSessionUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
) : SearchWithSessionUseCase {
    override suspend operator fun invoke(type: SearchWithSessionUseCase.Type): Either<SearchException, SearchWithSessionUseCase.Result> =
        withContext(dispatcher) {
            val searchSession = type.getSearchSession(searchRepository)
            // If a post is preloaded, return it right away
            searchSession.preloadedPost?.let { preloadedPost ->
                searchSession.usePreloadedPost(
                    preloadedPost = preloadedPost,
                    searchRepository = searchRepository
                )
                return@withContext Either.Right(
                    SearchWithSessionUseCase.Result(
                        searchSessionId = searchSession.id,
                        query = searchSession.query,
                        post = preloadedPost,
                        totalPosts = searchSession.totalPosts ?: 0
                    )
                )
            }
            // Else, run normal search
            searchWithHistoryUseCase(
                query = searchSession.query,
                totalPosts = searchSession.totalPosts,
                searchHistory = searchSession.searchHistory,
            )
                .fold(
                    ifLeft = {
                        when (it) {
                            is SearchException.Exhausted -> {
                                searchSession.clearExhaustedHistory(searchRepository)
                                invoke(type = type)
                            }

                            else -> Either.Left(it)
                        }
                    },
                    ifRight = { searchResult ->
                        searchSession.insertCurrentPost(
                            searchResult = searchResult,
                            searchRepository = searchRepository
                        )
                        Either.Right(
                            SearchWithSessionUseCase.Result(
                                searchSessionId = searchSession.id,
                                query = searchResult.query,
                                post = searchResult.post,
                                totalPosts = searchResult.totalPosts
                            )
                        )
                    }
                )
        }
}

private suspend fun SearchWithSessionUseCase.Type.getSearchSession(searchRepository: SearchRepository): SearchSession {
    val newSession = SearchSession(
        id = uuid4().toString(),
        query = this.query,
        totalPosts = null,
        searchHistory = emptyMap(),
        currentPost = null,
        preloadedPost = null,
        state = SearchSession.State.Searching
    )
    return when (this) {
        is SearchWithSessionUseCase.Type.NewSession -> newSession
        is SearchWithSessionUseCase.Type.WithSessionId -> searchRepository
            .getSearchSession(this.sessionId) ?: newSession
    }
}

private suspend fun SearchSession.insertCurrentPost(
    searchResult: SearchWithHistoryUseCase.Result,
    searchRepository: SearchRepository
) {
    val updatedSearchSession = copy(
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
    searchRepository.saveSearchSession(updatedSearchSession)
}

private suspend fun SearchSession.clearExhaustedHistory(searchRepository: SearchRepository) {
    val updatedSearchSession = copy(
        searchHistory = emptyMap(),
        currentPost = null,
    )
    searchRepository.saveSearchSession(updatedSearchSession)
}

private suspend fun SearchSession.usePreloadedPost(
    preloadedPost: Post,
    searchRepository: SearchRepository
) {
    val updatedSearchSession = copy(
        currentPost = preloadedPost,
        // Clear the preloaded post so that if it fails to load next time, we trigger search
        preloadedPost = null
    )
    searchRepository.saveSearchSession(updatedSearchSession)
}