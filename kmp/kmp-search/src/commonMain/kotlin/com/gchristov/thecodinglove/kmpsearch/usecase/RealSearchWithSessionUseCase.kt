package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpsearch.insert
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchType
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

internal class RealSearchWithSessionUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchUseCase: SearchUseCase,
) : SearchWithSessionUseCase {
    override suspend operator fun invoke(
        searchType: SearchType
    ): SearchWithSessionUseCase.Result = withContext(dispatcher) {
        val searchSession = getSearchSession(searchType)
        val searchResult = searchUseCase(
            query = searchSession.query,
            totalPosts = searchSession.totalPosts,
            searchHistory = searchSession.searchHistory,
        )
        when (searchResult) {
            is SearchUseCase.Result.Empty -> SearchWithSessionUseCase.Result.Empty
            is SearchUseCase.Result.Exhausted -> {
                clearSearchSessionHistory(searchSession)
                invoke(searchType = searchType)
            }

            is SearchUseCase.Result.Valid -> {
                insertSearchResultInSessionHistory(
                    searchSession = searchSession,
                    searchResult = searchResult
                )
                SearchWithSessionUseCase.Result.Valid(
                    searchSessionId = searchSession.id,
                    query = searchResult.query,
                    post = searchResult.post,
                    totalPosts = searchResult.totalPosts
                )
            }
        }
    }

    private suspend fun getSearchSession(searchType: SearchType): SearchSession {
        val newSession = SearchSession(
            id = Random.nextInt().toString(),
            query = searchType.query,
            totalPosts = null,
            searchHistory = emptyMap(),
            currentPost = null,
            state = SearchSession.State.Searching
        )
        return when (searchType) {
            is SearchType.NewSession -> newSession
            is SearchType.WithSessionId -> searchRepository
                .getSearchSession(searchType.sessionId) ?: newSession
        }
    }

    private suspend fun insertSearchResultInSessionHistory(
        searchSession: SearchSession,
        searchResult: SearchUseCase.Result.Valid
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
            currentPost = searchResult.post
        )
        searchRepository.saveSearchSession(updatedSearchSession)
    }

    private suspend fun clearSearchSessionHistory(searchSession: SearchSession) {
        val updatedSearchSession = searchSession.copy(searchHistory = emptyMap())
        searchRepository.saveSearchSession(updatedSearchSession)
    }
}