package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

/*
 Use-case to search for a random post, wrapping it within a search session. This use-case:
 • reuses or creates a new search session + search history
 */
class SearchWithSessionUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchUseCase: SearchUseCase
) {
    suspend operator fun invoke(
        searchType: SearchType,
        resultsPerPage: Int
    ): Result = withContext(dispatcher) {
        val searchSession = getSearchSession(searchType)
        val searchResult = searchUseCase(
            query = searchSession.query,
            totalPosts = searchSession.totalPosts,
            searchHistory = searchSession.searchHistory,
            resultsPerPage = resultsPerPage
        )
        when (searchResult) {
            is SearchUseCase.Result.Empty -> Result.Empty
            // TODO: Exhausted should clear history and try again
            is SearchUseCase.Result.Exhausted -> Result.Empty
            is SearchUseCase.Result.Valid -> {
                updateSearchSession(
                    searchSession = searchSession,
                    searchResult = searchResult
                )
                Result.Valid(
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
            searchHistory = emptyMap()
        )
        return when (searchType) {
            is SearchType.NewSearch -> newSession
            is SearchType.WithSessionId -> searchRepository
                .getSearchSession(searchType.sessionId) ?: newSession
        }
    }

    private suspend fun updateSearchSession(
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
            }
        )
        searchRepository.saveSearchSession(updatedSearchSession)
    }

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

    data class NewSearch(
        override val query: String,
    ) : SearchType()
}