package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import com.gchristov.thecodinglove.kmpsearchdata.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase
import com.gchristov.thecodinglove.kmpsearchtestfixtures.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RealPreloadSearchResultUseCaseTest {
    @Test
    fun preloadWithMissingSessionReturnsSessionNotFound(): TestResult {
        return runBlockingTest(searchSession = null) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = SearchSessionId)
            searchRepository.assertSessionFetched()
            searchWithHistoryUseCase.assertNotInvoked()
            assertEquals(
                expected = PreloadSearchResultUseCase.Result.SessionNotFound,
                actual = actualResult
            )
        }
    }

    @Test
    fun preloadWithSessionIdReusesSession(): TestResult {
        val searchResult = SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchSessionId = SearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun preloadWithEmptyResultReturnsEmpty(): TestResult {
        val searchResult = SearchWithHistoryUseCase.Result.Empty
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = SearchSessionId)
            searchRepository.assertSessionFetched()
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = PreloadSearchResultUseCase.Result.Empty,
                actual = actualResult,
            )
        }
    }

    @Test
    fun preloadWithExhaustedResultClearsSearchSessionHistoryAndRetries(): TestResult {
        val searchResults = listOf(
            SearchWithHistoryUseCase.Result.Exhausted,
            SearchWithHistoryUseCase.Result.Empty
        )
        val oldPreloadedPost = PostCreator.defaultPost()
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            searchHistory = mapOf(1 to listOf(0, 1, 2, 3)),
            preloadedPost = oldPreloadedPost
        )

        return runBlockingTest(
            multiSearchInvocationResults = searchResults,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchSessionId = SearchSessionId)
            searchWithHistoryUseCase.assertInvokedTwice()
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = searchSession.query,
                    totalPosts = null,
                    searchHistory = emptyMap(),
                    currentPost = oldPreloadedPost,
                    preloadedPost = null,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    @Test
    fun preloadUpdatesSessionAndReturnsSuccessResult(): TestResult {
        val searchResult = SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        val oldPreloadedPost = PostCreator.defaultPost()
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            preloadedPost = oldPreloadedPost
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = SearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = PreloadSearchResultUseCase.Result.Success,
                actual = actualResult
            )
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = SearchQuery,
                    totalPosts = searchResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = oldPreloadedPost,
                    preloadedPost = searchResult.post,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    @Test
    fun preloadKeepsCurrentPostIfNothingPreviouslyPreloaded(): TestResult {
        val searchResult = SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            preloadedPost = null
        )

        return runBlockingTest(
            singleSearchInvocationResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = SearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = PreloadSearchResultUseCase.Result.Success,
                actual = actualResult
            )
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = SearchQuery,
                    totalPosts = searchResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = searchSession.currentPost,
                    preloadedPost = searchResult.post,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    private fun runBlockingTest(
        singleSearchInvocationResult: SearchWithHistoryUseCase.Result? = null,
        multiSearchInvocationResults: List<SearchWithHistoryUseCase.Result>? = null,
        searchSession: SearchSession?,
        testBlock: suspend (PreloadSearchResultUseCase, FakeSearchRepository, FakeSearchWithHistoryUseCase) -> Unit
    ): TestResult = runTest {
        val searchInvocationResults = singleSearchInvocationResult?.let { listOf(it) }
            ?: multiSearchInvocationResults
            ?: emptyList()
        val searchRepository = FakeSearchRepository(searchSession = searchSession)
        val searchWithHistoryUseCase = FakeSearchWithHistoryUseCase(
            invocationResults = searchInvocationResults
        )
        val useCase = RealPreloadSearchResultUseCase(
            dispatcher = FakeCoroutineDispatcher,
            searchRepository = searchRepository,
            searchWithHistoryUseCase = searchWithHistoryUseCase
        )
        testBlock(useCase, searchRepository, searchWithHistoryUseCase)
    }
}

private const val SearchQuery = "test"
private const val SearchSessionId = "session_123"