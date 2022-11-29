package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchtestfixtures.FakeSearchRepository
import com.gchristov.thecodinglove.kmpsearchtestfixtures.PostCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchUseCaseTest {
    @Test
    fun searchWithNoResultsReturnsEmpty() = runBlockingTest(totalPosts = 0) {
        val actualResult = it.invoke(
            query = SearchQuery,
            searchHistory = mutableMapOf(),
            resultsPerPage = PostCreator.defaultPostPerPage()
        )
        assertEquals(
            expected = SearchUseCase.Result.Empty,
            actual = actualResult
        )
    }

    @Test
    fun searchWithEmptyResultsReturnsEmpty() = runBlockingTest(pages = emptyMap()) {
        val actualResult = it.invoke(
            query = SearchQuery,
            searchHistory = mutableMapOf(),
            resultsPerPage = PostCreator.defaultPostPerPage()
        )
        assertEquals(
            expected = SearchUseCase.Result.Empty,
            actual = actualResult,
        )
    }

    @Test
    fun searchWithOneResultReturnsPost() = runBlockingTest(
        totalPosts = 1,
        pages = PostCreator.singlePageSinglePost()
    ) {
        val actualResult = it.invoke(
            query = SearchQuery,
            searchHistory = mutableMapOf(),
            resultsPerPage = PostCreator.defaultPostPerPage()
        )
        assertEquals(
            expected = SearchUseCase.Result.Valid(
                query = SearchQuery,
                totalPosts = 1,
                post = PostCreator.singlePageSinglePost()[1]!!.first(),
                postPage = 1,
                postIndexOnPage = 0,
                postPageSize = 1
            ),
            actual = actualResult
        )
    }

    @Test
    fun searchExcludes() = runBlockingTest {
        val searchHistory = mutableMapOf<Int, List<Int>>()
        val minPostPage = 1
        val maxPostPage = 2
        val minPostIndexOnPage = 0
        val maxPostIndexOnPage = 3

        for (i in 0 until PostCreator.defaultTotalPosts()) {
            val actualResult = it.invoke(
                query = SearchQuery,
                searchHistory = searchHistory,
                resultsPerPage = PostCreator.defaultPostPerPage()
            ) as SearchUseCase.Result.Valid
            // Ensure post isn't already picked
            assertFalse {
                searchHistory.contains(
                    postPage = actualResult.postPage,
                    postIndexOnPage = actualResult.postIndexOnPage
                )
            }
            searchHistory.insert(
                postPage = actualResult.postPage,
                postIndexOnPage = actualResult.postIndexOnPage,
                currentPageSize = actualResult.postPageSize
            )
            // Ensure ranges
            assertTrue { actualResult.postPage in minPostPage..maxPostPage }
            assertTrue { actualResult.postIndexOnPage in minPostIndexOnPage..maxPostIndexOnPage }
        }
    }

    @Test
    fun searchExhausts() = runBlockingTest {
        val searchHistory = mutableMapOf<Int, List<Int>>()

        for (i in 0 until PostCreator.defaultTotalPosts()) {
            val actualResult = it.invoke(
                query = SearchQuery,
                searchHistory = searchHistory,
                resultsPerPage = PostCreator.defaultPostPerPage()
            ) as SearchUseCase.Result.Valid
            searchHistory.insert(
                postPage = actualResult.postPage,
                postIndexOnPage = actualResult.postIndexOnPage,
                currentPageSize = actualResult.postPageSize
            )
        }
        // Make sure we've exhausted all options
        assertTrue { searchHistory.size == PostCreator.multiPageMultiPost().size }
        for (page in PostCreator.multiPageMultiPost().keys) {
            assertTrue {
                val historyPage = searchHistory[page]!!
                val testPage = PostCreator.multiPageMultiPost()[page]!!
                historyPage.size - 1 == testPage.size
            }
        }
        // If all options are exhausted we shouldn't be able to search for an element
        val actualResult = it.invoke(
            query = SearchQuery,
            searchHistory = searchHistory,
            resultsPerPage = PostCreator.defaultPostPerPage()
        )
        assertTrue { actualResult == SearchUseCase.Result.Exhausted }
    }

    private fun runBlockingTest(
        totalPosts: Int = PostCreator.defaultTotalPosts(),
        pages: Map<Int, List<Post>> = PostCreator.multiPageMultiPost(),
        testBlock: suspend (SearchUseCase) -> Unit
    ) = runTest {
        val searchRepository = FakeSearchRepository(
            totalPosts = totalPosts,
            pages = pages
        )
        val useCase = RealSearchUseCase(
            dispatcher = FakeCoroutineDispatcher,
            searchRepository = searchRepository
        )
        testBlock(useCase)
    }
}

private const val SearchQuery = "test"