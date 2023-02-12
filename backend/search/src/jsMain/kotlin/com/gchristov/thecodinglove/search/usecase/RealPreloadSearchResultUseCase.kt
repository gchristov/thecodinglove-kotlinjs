package com.gchristov.thecodinglove.search.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchdata.model.SearchSession
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithHistoryUseCase
import com.gchristov.thecodinglove.searchdata.usecase.insert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RealPreloadSearchResultUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
) : PreloadSearchResultUseCase {
    override suspend operator fun invoke(searchSessionId: String): Either<SearchError, Unit> =
        withContext(dispatcher) {
            val searchSession = searchRepository
                .getSearchSession(searchSessionId)
                ?: return@withContext Either.Left(SearchError.SessionNotFound)
            searchWithHistoryUseCase(
                query = searchSession.query,
                totalPosts = searchSession.totalPosts,
                searchHistory = searchSession.searchHistory,
            )
                .fold(
                    ifLeft = {
                        if (it is SearchError.Exhausted) {
                            // Only clear the preloaded post and let session search deal with
                            // updating the history
                            searchSession.clearPreloadedPost(searchRepository)
                        }
                        Either.Left(it)
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
        preloadedPost = searchResult.post
    )
    searchRepository.saveSearchSession(updatedSearchSession)
}

private suspend fun SearchSession.clearPreloadedPost(searchRepository: SearchRepository) {
    val updatedSearchSession = copy(preloadedPost = null)
    searchRepository.saveSearchSession(updatedSearchSession)
}