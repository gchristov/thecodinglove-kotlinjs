package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 Use-case to search for a random post, wrapping it within a search session. This use-case:
 - reuses or creates a new search session + search history for the given query
 - searches for results for the given query, using the search session
 - if the search result is valid, updates the search history
 - if the search results are exhausted, clears the search history and retries the search
 */
interface SearchWithSessionUseCase {
    suspend operator fun invoke(
        searchType: SearchType,
        resultsPerPage: Int
    ): Result

    sealed class Result {
        object Empty : Result()
        data class Valid(
            val query: String,
            val post: Post,
            val totalPosts: Int
        ) : Result()
    }
}

sealed class SearchType {
    abstract val query: String

    data class WithSessionId(
        override val query: String,
        val sessionId: String
    ) : SearchType()

    data class NewSession(
        override val query: String,
    ) : SearchType()
}

internal class RealSearchWithSessionUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchUseCase: SearchUseCase
) : SearchWithSessionUseCase {
    override suspend operator fun invoke(
        searchType: SearchType,
        resultsPerPage: Int
    ): SearchWithSessionUseCase.Result = withContext(dispatcher) {
        val searchSession = getSearchSession(searchType)
        val searchResult = searchUseCase(
            query = searchSession.query,
            totalPosts = searchSession.totalPosts,
            searchHistory = searchSession.searchHistory,
            resultsPerPage = resultsPerPage
        )
        when (searchResult) {
            is SearchUseCase.Result.Empty -> SearchWithSessionUseCase.Result.Empty
            is SearchUseCase.Result.Exhausted -> {
                clearSearchSessionHistory(searchSession)
                invoke(
                    searchType = searchType,
                    resultsPerPage = resultsPerPage
                )
            }

            is SearchUseCase.Result.Valid -> {
                insertSearchResultInSessionHistory(
                    searchSession = searchSession,
                    searchResult = searchResult
                )
                SearchWithSessionUseCase.Result.Valid(
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