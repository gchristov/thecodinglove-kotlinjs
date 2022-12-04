package com.gchristov.thecodinglove.kmpsearch.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearchdata.SearchException
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
                expected = Either.Left(SearchException.SessionNotFound),
                actual = actualResult
            )
        }
    }

    @Test
    fun preloadWithSessionIdReusesSession(): TestResult {
        val searchWithHistoryResult = Either.Right(
            SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        )
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchSessionId = SearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun preloadWithEmptyResultReturnsEmpty(): TestResult {
        val searchWithHistoryResult = Either.Left(SearchException.Empty)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = SearchSessionId)
            searchRepository.assertSessionFetched()
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = searchWithHistoryResult,
                actual = actualResult,
            )
        }
    }

    @Test
    fun preloadWithExhaustedResultClearsSearchSessionHistoryAndRetries(): TestResult {
        val searchWithHistoryResults = listOf(
            Either.Left(SearchException.Exhausted),
            Either.Left(SearchException.Empty)
        )
        val oldPreloadedPost = PostCreator.defaultPost()
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            searchHistory = mapOf(1 to listOf(0, 1, 2, 3)),
            preloadedPost = oldPreloadedPost
        )

        return runBlockingTest(
            multiSearchWithHistoryInvocationResults = searchWithHistoryResults,
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
        val searchWithHistoryResult =
            SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        val oldPreloadedPost = PostCreator.defaultPost()
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            preloadedPost = oldPreloadedPost
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = Either.Right(searchWithHistoryResult),
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = SearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = Either.Right(Unit),
                actual = actualResult
            )
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = SearchQuery,
                    totalPosts = searchWithHistoryResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = oldPreloadedPost,
                    preloadedPost = searchWithHistoryResult.post,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    @Test
    fun preloadKeepsCurrentPostIfNothingPreviouslyPreloaded(): TestResult {
        val searchWithHistoryResult =
            SearchWithHistoryResultCreator.validResult(query = SearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            preloadedPost = null
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = Either.Right(searchWithHistoryResult),
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchSessionId = SearchSessionId)
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = Either.Right(Unit),
                actual = actualResult
            )
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = SearchQuery,
                    totalPosts = searchWithHistoryResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = searchSession.currentPost,
                    preloadedPost = searchWithHistoryResult.post,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    private fun runBlockingTest(
        singleSearchWithHistoryInvocationResult: Either<SearchException, SearchWithHistoryUseCase.Result>? = null,
        multiSearchWithHistoryInvocationResults: List<Either<SearchException, SearchWithHistoryUseCase.Result>>? = null,
        searchSession: SearchSession?,
        testBlock: suspend (PreloadSearchResultUseCase, FakeSearchRepository, FakeSearchWithHistoryUseCase) -> Unit
    ): TestResult = runTest {
        val searchInvocationResults = singleSearchWithHistoryInvocationResult?.let { listOf(it) }
            ?: multiSearchWithHistoryInvocationResults
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