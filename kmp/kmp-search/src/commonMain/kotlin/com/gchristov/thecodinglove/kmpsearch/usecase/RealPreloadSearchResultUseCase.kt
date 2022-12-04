package com.gchristov.thecodinglove.kmpsearch.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.kmpsearch.insert
import com.gchristov.thecodinglove.kmpsearchdata.SearchException
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import com.gchristov.thecodinglove.kmpsearchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class RealPreloadSearchResultUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
) : PreloadSearchResultUseCase {
    override suspend operator fun invoke(searchSessionId: String): Either<SearchException, Unit> =
        withContext(dispatcher) {
            val searchSession = searchRepository
                .getSearchSession(searchSessionId)
                ?: return@withContext Either.Left(SearchException.SessionNotFound)
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
                                invoke(searchSessionId = searchSessionId)
                            }

                            else -> Either.Left(it)
                        }
                    },
                    ifRight = { searchResult ->
                        searchSession.insertPreloadedPost(
                            searchResult = searchResult,
                            searchRepository = searchRepository
                        )
                        Either.Right(Unit)
                    }
                )
        }
}

private suspend fun SearchSession.insertPreloadedPost(
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
        // The old preloaded post now becomes the current one, if found, and a new future one is set
        currentPost = preloadedPost ?: currentPost,
        preloadedPost = searchResult.post
    )
    searchRepository.saveSearchSession(updatedSearchSession)
}

private suspend fun SearchSession.clearExhaustedHistory(searchRepository: SearchRepository) {
    val updatedSearchSession = copy(
        searchHistory = emptyMap(),
        // When clearing we still want to set the current post to whatever is the preloaded one
        currentPost = preloadedPost,
        preloadedPost = null
    )
    searchRepository.saveSearchSession(updatedSearchSession)
}