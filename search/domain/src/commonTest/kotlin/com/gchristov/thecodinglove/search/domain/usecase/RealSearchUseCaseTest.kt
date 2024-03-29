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
            useCase.invoke(SearchUseCase.Dto(searchType))
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionNotFetched()
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
            useCase.invoke(SearchUseCase.Dto(searchType))
            searchWithHistoryUseCase.assertInvokedOnce()
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun searchWithSessionIdReturnsPreloadedPostAndPreloads(): TestResult {
        val searchType = SearchUseCase.Type.WithSessionId(TestSearchSessionId)
        val searchWithHistoryResult = Either.Right(
            SearchWithHistoryResultCreator.validResult(query = TestSearchQuery)
        )
        val preloadedPost = SearchPostCreator.defaultPost()
        val searchSession = SearchSessionCreator.searchSession(
            id = TestSearchSessionId,
            query = TestSearchQuery,
            preloadedPost = preloadedPost
        )

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = searchSession,
        ) { useCase, searchRepository, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(SearchUseCase.Dto(searchType))
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
    fun searchWithEmptyResultReturnsEmptyAndDoesNotPreload(): TestResult {
        val searchType = SearchUseCase.Type.NewSession(query = TestSearchQuery)
        val searchWithHistoryResult = Either.Left(SearchWithHistoryUseCase.Error.Empty(additionalInfo = "test"))

        return runBlockingTest(
            singleSearchWithHistoryInvocationResult = searchWithHistoryResult,
            searchSession = null,
        ) { useCase, _, searchWithHistoryUseCase ->
            val actualResult = useCase.invoke(SearchUseCase.Dto(searchType))
            searchWithHistoryUseCase.assertInvokedOnce()
            assertEquals(
                expected = SearchUseCase.Error.Empty(additionalInfo = "test"),
                actual = actualResult.leftOrNull(),
            )
        }
    }

    @Test
    fun searchWithExhaustedResultClearsSearchSessionHistoryAndRetries(): TestResult {
        val searchType = SearchUseCase.Type.WithSessionId(TestSearchSessionId)
        val searchWithHistoryResults = listOf(
            Either.Left(SearchWithHistoryUseCase.Error.Exhausted(additionalInfo = "test")),
            Either.Left(SearchWithHistoryUseCase.Error.Empty(additionalInfo = "test"))
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
            useCase.invoke(SearchUseCase.Dto(searchType))
            searchWithHistoryUseCase.assertInvokedTwice()
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = searchSession.id,
                    query = searchSession.query,
                    totalPosts = null,
                    searchHistory = emptyMap(),
                    currentPost = null,
                    preloadedPost = null,
                    state = SearchSession.State.Searching()
                )
            )
        }
    }

    @Test
    fun searchUpdatesSessionAndReturnsValidResultAndPreloads(): TestResult {
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
            val actualResult = useCase.invoke(SearchUseCase.Dto(searchType))
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
                    state = SearchSession.State.Searching()
                )
            )
        }
    }

    private fun runBlockingTest(
        singleSearchWithHistoryInvocationResult: Either<SearchWithHistoryUseCase.Error, SearchWithHistoryUseCase.Result>? = null,
        multiSearchWithHistoryInvocationResults: List<Either<SearchWithHistoryUseCase.Error, SearchWithHistoryUseCase.Result>>? = null,
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
            searchWithHistoryUseCase = searchWithHistoryUseCase,
            log = FakeLogger,
        )
        testBlock(useCase, searchRepository, searchWithHistoryUseCase)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"