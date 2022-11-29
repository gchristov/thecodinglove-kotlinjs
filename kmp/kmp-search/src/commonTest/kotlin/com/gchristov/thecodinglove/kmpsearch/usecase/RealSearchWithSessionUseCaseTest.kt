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

@OptIn(ExperimentalCoroutinesApi::class)
class RealSearchWithSessionUseCaseTest {
    @Test
    fun searchWithNewSessionCreatesNewSession() =
        runBlockingTest(
            searchResult = SearchResultCreator.validResult(query = SearchQuery),
            searchSession = null,
        ) { useCase, searchRepository ->
            SearchResultCreator.validResult(query = SearchQuery)
            useCase.invoke(
                searchType = SearchType.NewSession(query = SearchQuery),
                resultsPerPage = PostCreator.defaultPostPerPage()
            ) as SearchWithSessionUseCase.Result.Valid
            searchRepository.assertSessionNotFetched()
        }

    @Test
    fun searchWithSessionIdReusesSession() =
        runBlockingTest(
            searchResult = SearchResultCreator.validResult(query = SearchQuery),
            searchSession = SearchSessionCreator.searchSession(
                id = SearchSessionId,
                query = SearchQuery
            ),
        ) { useCase, searchRepository ->
            useCase.invoke(
                searchType = SearchType.WithSessionId(
                    query = SearchQuery,
                    sessionId = SearchSessionId
                ),
                resultsPerPage = PostCreator.defaultPostPerPage()
            ) as SearchWithSessionUseCase.Result.Valid
            searchRepository.assertSessionFetched()
        }

    @Test
    fun searchUpdatesSession() =
        runBlockingTest(
            searchResult = SearchResultCreator.validResult(query = SearchQuery),
            searchSession = null,
        ) { useCase, searchRepository ->
            val searchResult = SearchResultCreator.validResult(query = SearchQuery)
            val actualResult = useCase.invoke(
                searchType = SearchType.NewSession(query = SearchQuery),
                resultsPerPage = PostCreator.defaultPostPerPage()
            ) as SearchWithSessionUseCase.Result.Valid
            searchRepository.assertSessionSaved(
                SearchSession(
                    id = actualResult.searchSessionId,
                    query = SearchQuery,
                    totalPosts = searchResult.totalPosts,
                    searchHistory = mapOf(1 to listOf(0, -1)),
                    currentPost = searchResult.post,
                    state = SearchSession.State.Searching
                )
            )
        }

//    @Test
//    fun searchWithEmptyResultsReturnsEmpty() = runBlockingTest(pages = emptyMap()) {
//        val actualResult = it.invoke(
//            query = SearchQuery,
//            searchHistory = mutableMapOf(),
//            resultsPerPage = PostCreator.defaultPostPerPage()
//        )
//        assertEquals(
//            expected = SearchUseCase.Result.Empty,
//            actual = actualResult,
//        )
//    }
//
//    @Test
//    fun searchWithOneResultReturnsPost() = runBlockingTest(
//        totalPosts = 1,
//        pages = PostCreator.singlePageSinglePost()
//    ) {
//        val actualResult = it.invoke(
//            query = SearchQuery,
//            searchHistory = mutableMapOf(),
//            resultsPerPage = PostCreator.defaultPostPerPage()
//        )
//        assertEquals(
//            expected = SearchUseCase.Result.Valid(
//                query = SearchQuery,
//                totalPosts = 1,
//                post = PostCreator.singlePageSinglePost()[1]!!.first(),
//                postPage = 1,
//                postIndexOnPage = 0,
//                postPageSize = 1
//            ),
//            actual = actualResult
//        )
//    }
//
//    @Test
//    fun searchExcludes() = runBlockingTest {
//        val searchHistory = mutableMapOf<Int, List<Int>>()
//        val minPostPage = 1
//        val maxPostPage = 2
//        val minPostIndexOnPage = 0
//        val maxPostIndexOnPage = 3
//
//        for (i in 0 until PostCreator.defaultTotalPosts()) {
//            val actualResult = it.invoke(
//                query = SearchQuery,
//                searchHistory = searchHistory,
//                resultsPerPage = PostCreator.defaultPostPerPage()
//            ) as SearchUseCase.Result.Valid
//            // Ensure post isn't already picked
//            assertFalse {
//                searchHistory.contains(
//                    postPage = actualResult.postPage,
//                    postIndexOnPage = actualResult.postIndexOnPage
//                )
//            }
//            searchHistory.insert(
//                postPage = actualResult.postPage,
//                postIndexOnPage = actualResult.postIndexOnPage,
//                currentPageSize = actualResult.postPageSize
//            )
//            // Ensure ranges
//            assertTrue { actualResult.postPage in minPostPage..maxPostPage }
//            assertTrue { actualResult.postIndexOnPage in minPostIndexOnPage..maxPostIndexOnPage }
//        }
//    }
//
//    @Test
//    fun searchExhausts() = runBlockingTest {
//        val searchHistory = mutableMapOf<Int, List<Int>>()
//
//        for (i in 0 until PostCreator.defaultTotalPosts()) {
//            val actualResult = it.invoke(
//                query = SearchQuery,
//                searchHistory = searchHistory,
//                resultsPerPage = PostCreator.defaultPostPerPage()
//            ) as SearchUseCase.Result.Valid
//            searchHistory.insert(
//                postPage = actualResult.postPage,
//                postIndexOnPage = actualResult.postIndexOnPage,
//                currentPageSize = actualResult.postPageSize
//            )
//        }
//        // Make sure we've exhausted all options
//        assertTrue { searchHistory.size == PostCreator.multiPageMultiPost().size }
//        for (page in PostCreator.multiPageMultiPost().keys) {
//            assertTrue {
//                val historyPage = searchHistory[page]!!
//                val testPage = PostCreator.multiPageMultiPost()[page]!!
//                historyPage.size - 1 == testPage.size
//            }
//        }
//        // If all options are exhausted we shouldn't be able to search for an element
//        val actualResult = it.invoke(
//            query = SearchQuery,
//            searchHistory = searchHistory,
//            resultsPerPage = PostCreator.defaultPostPerPage()
//        )
//        assertTrue { actualResult == SearchUseCase.Result.Exhausted }
//    }

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