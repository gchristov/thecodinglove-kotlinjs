package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import com.gchristov.thecodinglove.searchtestfixtures.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RealPreloadSearchResultUseCaseTest {
    @Test
    fun preloadWithMissingSessionReturnsSessionNotFound(): TestResult {
        return runBlockingTest(searchSession = null) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = TestSearchSessionId)
            searchRepository.assertSessionFetched()
            searchWithHistoryUseCase.assertNotInvoked()
            assertEquals(
                expected = Either.Left(SearchError.SessionNotFound(additionalInfo = "Session not found")),
                actual = actualResult
            )
        }
    }

    @Test
    fun preloadWithSessionIdReusesSession(): TestResult {
        val searchWithHistoryResult = Either.Right(
            SearchWithHistoryResultCreator.validResult(query = TestSearchQuery)
        )
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchSessionId = TestSearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun preloadWithEmptyResultReturnsEmpty(): TestResult {
        val searchWithHistoryResult = Either.Left(SearchError.Empty(additionalInfo = "test"))
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = TestSearchSessionId)
            searchRepository.assertSessionFetched()
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = Either.Left(SearchError.Empty(additionalInfo = "test")),
                actual = actualResult,
            )
        }
    }

    @Test
    fun preloadWithExhaustedResultClearsPreloadedPostAndReturnsExhausted(): TestResult {
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery,
            searchHistory = mapOf(1 to listOf(0, 1, 2, 3)),
            preloadedPost = PostCreator.defaultPost()
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = Either.Left(SearchError.Exhausted(additionalInfo = "test")),
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = TestSearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = searchSession.query,
                    totalPosts = searchSession.totalPosts,
                    searchHistory = searchSession.searchHistory,
                    currentPost = searchSession.currentPost,
                    preloadedPost = null,
                    state = SearchSession.State.Searching()
                )
            )
            assertEquals(
                expected = Either.Left(SearchError.Exhausted(additionalInfo = "test")),
                actual = actualResult,
            )
        }
    }

    @Test
    fun preloadUpdatesSessionAndReturnsSuccessResult(): TestResult {
        val searchWithHistoryResult =
            SearchWithHistoryResultCreator.validResult(query = TestSearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = Either.Right(searchWithHistoryResult),
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = TestSearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = searchSession.query,
                    totalPosts = searchWithHistoryResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = searchSession.currentPost,
                    preloadedPost = searchWithHistoryResult.post,
                    state = SearchSession.State.Searching()
                )
            )
            assertEquals(
                expected = Either.Right(Unit),
                actual = actualResult
            )
        }
    }

    private fun runBlockingTest(
        singleSearchWithHistoryInvocationResult: Either<SearchError, SearchWithHistoryUseCase.Result>? = null,
        searchSession: SearchSession?,
        testBlock: suspend (PreloadSearchResultUseCase, FakeSearchRepository, FakeSearchWithHistoryUseCase) -> Unit
    ): TestResult = runTest {
        val searchInvocationResults = singleSearchWithHistoryInvocationResult?.let { listOf(it) }
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

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"