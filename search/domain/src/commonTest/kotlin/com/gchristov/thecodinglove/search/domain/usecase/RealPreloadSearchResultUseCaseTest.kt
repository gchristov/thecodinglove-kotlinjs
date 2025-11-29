package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.testfixtures.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealPreloadSearchResultUseCaseTest {
    @Test
    fun preloadWithMissingSessionReturnsSessionNotFound(): TestResult {
        return runBlockingTest(searchSession = null) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(PreloadSearchResultUseCase.Dto(searchSessionId = TestSearchSessionId))
            searchRepository.assertSessionFetched()
            searchWithHistoryUseCase.assertNotInvoked()
            assertTrue { actualResult.isLeft { it.message == "Search session not found: searchSessionId=$TestSearchSessionId" } }
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
            useCase.invoke(PreloadSearchResultUseCase.Dto(searchSessionId = TestSearchSessionId))
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun preloadWithEmptyResultReturnsEmpty(): TestResult {
        val searchWithHistoryResult = Either.Right(SearchWithHistoryUseCase.Result.Empty)
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(PreloadSearchResultUseCase.Dto(searchSessionId = TestSearchSessionId))
            searchRepository.assertSessionFetched()
            searchRepository.assertSessionNotSaved()
            searchWithHistoryUseCase.assertInvokedOnce()
            assertTrue { actualResult.isRight() }
        }
    }

    @Test
    fun preloadWithExhaustedResultClearsPreloadedPostAndReturns(): TestResult {
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery,
            searchHistory = mapOf(1 to listOf(0, 1, 2, 3)),
            preloadedPost = SearchPostCreator.defaultPost()
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = Either.Right(
                SearchWithHistoryUseCase.Result.Exhausted
            ),
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(PreloadSearchResultUseCase.Dto(searchSessionId = TestSearchSessionId))
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
                expected = Either.Right(Unit),
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
            val actualResult = useCase.invoke(PreloadSearchResultUseCase.Dto(searchSessionId = TestSearchSessionId))
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
        singleSearchWithHistoryInvocationResult: Either<Throwable, SearchWithHistoryUseCase.Result>? = null,
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
            searchWithHistoryUseCase = searchWithHistoryUseCase,
            log = FakeLogger,
        )
        testBlock(useCase, searchRepository, searchWithHistoryUseCase)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"