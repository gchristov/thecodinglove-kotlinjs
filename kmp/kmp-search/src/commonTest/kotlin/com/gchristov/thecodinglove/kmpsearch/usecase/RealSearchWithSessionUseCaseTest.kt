package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchType
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.kmpsearchtestfixtures.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RealSearchWithSessionUseCaseTest {
    @Test
    fun searchWithNewSessionCreatesNewSession(): TestResult {
        val searchType = SearchType.NewSession(query = SearchQuery)
        val searchResult = SearchResultCreator.validResult(query = SearchQuery)

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = null,
        ) { useCase, searchRepository, searchUseCase ->
            useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            searchUseCase.assertInvokedOnce()
            searchRepository.assertSessionNotFetched()
        }
    }

    @Test
    fun searchWithSessionIdReusesSession(): TestResult {
        val searchType = SearchType.WithSessionId(
            query = SearchQuery,
            sessionId = SearchSessionId
        )
        val searchResult = SearchResultCreator.validResult(query = SearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchUseCase ->
            useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            searchUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun searchWithEmptyResultReturnsEmpty(): TestResult {
        val searchType = SearchType.NewSession(query = SearchQuery)
        val searchResult = SearchUseCase.Result.Empty

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = null,
        ) { useCase, _, searchUseCase ->
            val actualResult = useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            searchUseCase.assertInvokedOnce()
            assertEquals(
                expected = SearchWithSessionUseCase.Result.Empty,
                actual = actualResult,
            )
        }
    }

    @Test
    fun searchWithExhaustedResultClearsSearchSessionHistoryAndRetries(): TestResult {
        val searchType = SearchType.WithSessionId(
            query = SearchQuery,
            sessionId = SearchSessionId
        )
        val searchResults = listOf(
            SearchUseCase.Result.Exhausted,
            SearchUseCase.Result.Empty
        )
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            searchHistory = mapOf(1 to listOf(0, 1, 2, 3))
        )

        return runBlockingTest(
            singleSearchInvocationResult = null,
            multiSearchInvocationResults = searchResults,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchUseCase ->
            useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            searchUseCase.assertInvokedTwice()
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = searchSession.query,
                    totalPosts = null,
                    searchHistory = emptyMap(),
                    currentPost = null,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    @Test
    fun searchUpdatesSessionAndReturnsValidResult(): TestResult {
        val searchType = SearchType.WithSessionId(
            sessionId = SearchSessionId,
            query = SearchQuery
        )
        val searchResult = SearchResultCreator.validResult(query = SearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )
        val expectedSearchWithSessionResult = SearchWithSessionUseCase.Result.Valid(
            searchSessionId = searchSession.id,
            query = searchSession.query,
            post = searchResult.post,
            totalPosts = searchResult.totalPosts
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchUseCase ->
            val actualResult = useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            searchUseCase.assertInvokedOnce()
            assertEquals(
                expected = expectedSearchWithSessionResult,
                actual = actualResult
            )
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = SearchQuery,
                    totalPosts = searchResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = searchResult.post,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    private fun runBlockingTest(
        singleSearchInvocationResult: SearchUseCase.Result? = null,
        multiSearchInvocationResults: List<SearchUseCase.Result>? = null,
        searchSession: SearchSession?,
        testBlock: suspend (SearchWithSessionUseCase, FakeSearchRepository, FakeSearchUseCase) -> Unit
    ): TestResult = runTest {
        val searchInvocationResults = singleSearchInvocationResult?.let { listOf(it) }
            ?: multiSearchInvocationResults
            ?: emptyList()
        val searchRepository = FakeSearchRepository(searchSession = searchSession)
        val searchUseCase = FakeSearchUseCase(invocationResults = searchInvocationResults)
        val useCase = RealSearchWithSessionUseCase(
            dispatcher = FakeCoroutineDispatcher,
            searchRepository = searchRepository,
            searchUseCase = searchUseCase
        )
        testBlock(useCase, searchRepository, searchUseCase)
    }
}

private const val SearchQuery = "test"
private const val SearchSessionId = "session_123"