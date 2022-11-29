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
            searchResult = searchResult,
            searchSession = null,
        ) { useCase, searchRepository ->
            useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
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
            searchResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository ->
            useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            searchRepository.assertSessionFetched()
        }
    }

    @Test
    fun searchWithEmptyResultsReturnsEmpty() {
        val searchType = SearchType.NewSession(query = SearchQuery)
        val searchResult = SearchUseCase.Result.Empty

        runBlockingTest(
            searchResult = searchResult,
            searchSession = null,
        ) { useCase, _ ->
            val actualResult = useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
            assertEquals(
                expected = SearchWithSessionUseCase.Result.Empty,
                actual = actualResult,
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
            searchResult = searchResult,
            searchSession = searchSession,
        ) { useCase, searchRepository ->
            val actualResult = useCase.invoke(
                searchType = searchType,
                resultsPerPage = PostCreator.defaultPostPerPage()
            )
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
        searchResult: SearchUseCase.Result,
        searchSession: SearchSession?,
        testBlock: suspend (SearchWithSessionUseCase, FakeSearchRepository) -> Unit
    ) = runTest {
        val searchRepository = FakeSearchRepository(searchSession = searchSession)
        val searchUseCase = FakeSearchUseCase(result = searchResult)
        val useCase = RealSearchWithSessionUseCase(
            dispatcher = FakeCoroutineDispatcher,
            searchRepository = searchRepository,
            searchUseCase = searchUseCase
        )
        testBlock(useCase, searchRepository)
    }
}

private const val SearchQuery = "test"
private const val SearchSessionId = "session_123"