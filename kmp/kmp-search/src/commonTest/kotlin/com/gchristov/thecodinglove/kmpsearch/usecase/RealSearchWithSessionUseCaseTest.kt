package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchType
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.kmpsearchtestfixtures.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RealSearchWithSessionUseCaseTest {
    @Test
    fun searchWithNewSessionCreatesNewSession() {
        val searchType = SearchType.NewSession(query = SearchQuery)
        val searchResult = SearchResultCreator.validResult(query = SearchQuery)

        runBlockingTest(
            singleSearchInvokationResult = searchResult,
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
    fun searchWithSessionIdReusesSession() {
        val searchType = SearchType.WithSessionId(
            query = SearchQuery,
            sessionId = SearchSessionId
        )
        val searchResult = SearchResultCreator.validResult(query = SearchQuery)
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery
        )

        runBlockingTest(
            singleSearchInvokationResult = searchResult,
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
    fun searchWithEmptyResultReturnsEmpty() {
        val searchType = SearchType.NewSession(query = SearchQuery)
        val searchResult = SearchUseCase.Result.Empty

        runBlockingTest(
            singleSearchInvokationResult = searchResult,
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
    fun searchWithExhaustedResultClearsSearchSessionHistoryAndRetries() {
        val searchType = SearchType.NewSession(query = SearchQuery)
        val searchResults = listOf(
            SearchUseCase.Result.Exhausted,
            SearchUseCase.Result.Empty
        )
        val searchSession = SearchSessionCreator.searchSession(
            id = SearchSessionId,
            query = SearchQuery,
            searchHistory = mapOf(1 to listOf(0, 1, 2, 3))
        )

        runBlockingTest(
            singleSearchInvokationResult = null,
            multiSearchInvokationResults = searchResults,
            searchSession = searchSession,
        ) { useCase, _, searchUseCase ->
            useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            searchUseCase.assertInvokedTwice()
            assertEquals(
                expected = emptyMap(),
                actual = searchSession.searchHistory,
            )
        }
    }

    @Test
    fun searchUpdatesSessionAndReturnsValidResult() {
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

        runBlockingTest(
            singleSearchInvokationResult = searchResult,
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
        singleSearchInvokationResult: SearchUseCase.Result? = null,
        multiSearchInvokationResults: List<SearchUseCase.Result>? = null,
        searchSession: SearchSession?,
        testBlock: suspend (SearchWithSessionUseCase, FakeSearchRepository, FakeSearchUseCase) -> Unit
    ) = runTest {
        val searchInvokationResults = singleSearchInvokationResult?.let { listOf(it) }
            ?: multiSearchInvokationResults
            ?: emptyList()
        val searchRepository = FakeSearchRepository(searchSession = searchSession)
        val searchUseCase = FakeSearchUseCase(invokationResults = searchInvokationResults)
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