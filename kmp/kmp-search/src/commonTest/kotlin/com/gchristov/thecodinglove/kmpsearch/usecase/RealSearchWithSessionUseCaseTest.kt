package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchType
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase
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
        val searchResult = SearchWithHistoryResultCreator.validResult(query = SearchQuery)

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = null,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionNotFetched()
        }
    }

    @Test
    fun searchWithSessionIdReturnsPreloadedPost(): TestResult {
        val searchType = SearchType.WithSessionId(
            query = SearchQuery,
            sessionId = SearchSessionId
        )
        val searchResult = SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        val preloadedPost = PostCreator.defaultPost()
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            preloadedPost = preloadedPost
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchType = searchType)
            searchWithHistoryUseCase.assertNotInvoked()
            searchRepository.assertSessionFetched()
            assertEquals(
                expected = SearchWithSessionUseCase.Result.Valid(
                    searchSessionId = searchSession.id,
                    query = searchSession.query,
                    post = preloadedPost,
                    totalPosts = searchSession.totalPosts ?: 0
                ),
                actual = actualResult
            )
        }
    }

    @Test
    fun searchWithSessionIdReusesSession(): TestResult {
        val searchType = SearchType.WithSessionId(
            query = SearchQuery,
            sessionId = SearchSessionId
        )
        val searchResult = SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchType = searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun searchWithEmptyResultReturnsEmpty(): TestResult {
        val searchType = SearchType.NewSession(query = SearchQuery)
        val searchResult = SearchWithHistoryUseCase.Result.Empty

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = null,
        ) { useCase, _, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
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
            SearchWithHistoryUseCase.Result.Exhausted,
            SearchWithHistoryUseCase.Result.Empty
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
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchType)
            searchWithHistoryUseCase.assertInvokedTwice()
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = searchSession.query,
                    totalPosts = null,
                    searchHistory = emptyMap(),
                    currentPost = null,
                    preloadedPost = null,
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
        val searchResult = SearchWithHistoryResultCreator.validResult(query = SearchQuery)
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
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
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
                    preloadedPost = null,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    private fun runBlockingTest(
        singleSearchInvocationResult: SearchWithHistoryUseCase.Result? = null,
        multiSearchInvocationResults: List<SearchWithHistoryUseCase.Result>? = null,
        searchSession: SearchSession?,
        testBlock: suspend (SearchWithSessionUseCase, FakeSearchRepository, FakeSearchWithHistoryUseCase) -> Unit
    ): TestResult = runTest {
        val searchInvocationResults = singleSearchInvocationResult?.let { listOf(it) }
            ?: multiSearchInvocationResults
            ?: emptyList()
        val searchRepository = FakeSearchRepository(searchSession = searchSession)
        val searchWithHistoryUseCase = FakeSearchWithHistoryUseCase(
            invocationResults = searchInvocationResults
        )
        val useCase = RealSearchWithSessionUseCase(
            dispatcher = FakeCoroutineDispatcher,
            searchRepository = searchRepository,
            searchWithHistoryUseCase = searchWithHistoryUseCase
        )
        testBlock(useCase, searchRepository, searchWithHistoryUseCase)
    }
}

private const val SearchQuery = "test"
private const val SearchSessionId = "session_123"