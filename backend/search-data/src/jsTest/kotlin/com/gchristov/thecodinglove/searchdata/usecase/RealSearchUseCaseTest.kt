package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchdata.model.SearchSession
import com.gchristov.thecodinglove.searchtestfixtures.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RealSearchUseCaseTest {
    @Test
    fun searchWithNewSessionCreatesNewSession(): TestResult {
        val searchType = SearchUseCase.Type.NewSession(query = TestSearchQuery)
        val searchWithHistoryResult = Either.Right(
            SearchWithHistoryResultCreator.validResult(query = TestSearchQuery)
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = null,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            useCase.invoke(searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionNotFetched()
        }
    }

    @Test
    fun searchWithSessionIdReturnsPreloadedPost(): TestResult {
        val searchType = SearchUseCase.Type.WithSessionId(TestSearchSessionId)
        val searchWithHistoryResult = Either.Right(
            SearchWithHistoryResultCreator.validResult(query = TestSearchQuery)
        )
        val preloadedPost = PostCreator.defaultPost()
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery,
            preloadedPost = preloadedPost
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(type = searchType)
            searchWithHistoryUseCase.assertNotInvoked()
            searchRepository.assertSessionFetched()
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = searchSession.query,
                    totalPosts = searchSession.totalPosts,
                    searchHistory = searchSession.searchHistory,
                    currentPost = preloadedPost,
                    preloadedPost = null,
                    state = searchSession.state
                )
            )
            assertEquals(
                expected = Either.Right(
                    SearchUseCase.Result(
                        searchSessionId = searchSession.id,
                        query = searchSession.query,
                        post = preloadedPost,
                        totalPosts = searchSession.totalPosts ?: 0
                    )
                ),
                actual = actualResult
            )
        }
    }

    @Test
    fun searchWithSessionIdReusesSession(): TestResult {
        val searchType = SearchUseCase.Type.WithSessionId(TestSearchSessionId)
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
            useCase.invoke(type = searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun searchWithEmptyResultReturnsEmpty(): TestResult {
        val searchType = SearchUseCase.Type.NewSession(query = TestSearchQuery)
        val searchWithHistoryResult = Either.Left(SearchError.Empty)

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = null,
        ) { useCase, _, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = searchWithHistoryResult,
                actual = actualResult,
            )
        }
    }

    @Test
    fun searchWithExhaustedResultClearsSearchSessionHistoryAndRetries(): TestResult {
        val searchType = SearchUseCase.Type.WithSessionId(TestSearchSessionId)
        val searchWithHistoryResults = listOf(
            Either.Left(SearchError.Exhausted),
            Either.Left(SearchError.Empty)
        )
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery,
            searchHistory = mapOf(1 to listOf(0, 1, 2, 3))
        )

        return runBlockingTest(
            multiSearchWithHistoryInvocationResults = searchWithHistoryResults,
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
        val searchType = SearchUseCase.Type.WithSessionId(TestSearchSessionId)
        val searchWithHistoryResult = SearchWithHistoryResultCreator.validResult(
            query = TestSearchQuery
        )
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery
        )
        val expectedSearchResult = SearchUseCase.Result(
            searchSessionId = searchSession.id,
            query = searchSession.query,
            post = searchWithHistoryResult.post,
            totalPosts = searchWithHistoryResult.totalPosts
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = Either.Right(searchWithHistoryResult),
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(searchType)
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = Either.Right(expectedSearchResult),
                actual = actualResult
            )
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = TestSearchQuery,
                    totalPosts = searchWithHistoryResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = searchWithHistoryResult.post,
                    preloadedPost = null,
                    state = SearchSession.State.Searching
                )
            )
        }
    }

    private fun runBlockingTest(
        singleSearchWithHistoryInvocationResult: Either<SearchError, SearchWithHistoryUseCase.Result>? = null,
        multiSearchWithHistoryInvocationResults: List<Either<SearchError, SearchWithHistoryUseCase.Result>>? = null,
        searchSession: SearchSession?,
        testBlock: suspend (SearchUseCase, FakeSearchRepository, FakeSearchWithHistoryUseCase) -> Unit
    ): TestResult = runTest {
        val searchInvocationResults = singleSearchWithHistoryInvocationResult?.let { listOf(it) }
            ?: multiSearchWithHistoryInvocationResults
            ?: emptyList()
        val searchRepository = FakeSearchRepository(searchSession = searchSession)
        val searchWithHistoryUseCase = FakeSearchWithHistoryUseCase(
            invocationResults = searchInvocationResults
        )
        val useCase = RealSearchUseCase(
            dispatcher = FakeCoroutineDispatcher,
            searchRepository = searchRepository,
            searchWithHistoryUseCase = searchWithHistoryUseCase
        )
        testBlock(useCase, searchRepository, searchWithHistoryUseCase)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"