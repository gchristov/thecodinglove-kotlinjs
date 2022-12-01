package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpsearch.insert
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
    override suspend operator fun invoke(
        searchSessionId: String
    ): PreloadSearchResultUseCase.Result = withContext(dispatcher) {
        val searchSession = searchRepository
            .getSearchSession(searchSessionId)
            ?: return@withContext PreloadSearchResultUseCase.Result.SessionNotFound
        val searchResult = searchWithHistoryUseCase(
            query = searchSession.query,
            totalPosts = searchSession.totalPosts,
            searchHistory = searchSession.searchHistory,
        )
        when (searchResult) {
            is SearchWithHistoryUseCase.Result.Empty -> PreloadSearchResultUseCase.Result.Empty
            is SearchWithHistoryUseCase.Result.Exhausted -> {
                clearSearchSessionHistory(searchSession)
                invoke(searchSessionId = searchSessionId)
            }

            is SearchWithHistoryUseCase.Result.Valid -> {
                insertSearchResultInSession(
                    searchSession = searchSession,
                    searchResult = searchResult,
                )
                PreloadSearchResultUseCase.Result.Success
            }
        }
    }

    private suspend fun insertSearchResultInSession(
        searchSession: SearchSession,
        searchResult: SearchWithHistoryUseCase.Result.Valid,
    ) {
        val updatedSearchSession = searchSession.copy(
            totalPosts = searchResult.totalPosts,
            searchHistory = searchSession.searchHistory.toMutableMap().apply {
                insert(
                    postPage = searchResult.postPage,
                    postIndexOnPage = searchResult.postIndexOnPage,
                    currentPageSize = searchResult.postPageSize
                )
            },
            // The old preloaded post now becomes the current one, if found, and a new future one is set
            currentPost = searchSession.preloadedPost ?: searchSession.currentPost,
            preloadedPost = searchResult.post
        )
        searchRepository.saveSearchSession(updatedSearchSession)
    }

    private suspend fun clearSearchSessionHistory(searchSession: SearchSession) {
        val updatedSearchSession = searchSession.copy(
            searchHistory = emptyMap(),
            // When clearing we still want to set the current post to whatever is the preloaded one
            currentPost = searchSession.preloadedPost,
            preloadedPost = null
        )
        searchRepository.saveSearchSession(updatedSearchSession)
    }
}