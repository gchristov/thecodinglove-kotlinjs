package com.gchristov.thecodinglove.kmpsearch.usecase

import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpsearch.contains
import com.gchristov.thecodinglove.kmpsearch.insert
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.kmpsearchtestfixtures.FakeSearchRepository
import com.gchristov.thecodinglove.kmpsearchtestfixtures.PostCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RealSearchUseCaseTest {
    @Test
    fun searchWithNoResultsReturnsEmpty(): TestResult {
        val totalPosts = 0
        val resultsPerPage = PostCreator.multiPageMultiPostPageSize()
        val pages = PostCreator.multiPageMultiPost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                query = SearchQuery,
                searchHistory = mutableMapOf(),
                resultsPerPage = resultsPerPage
            )
            assertEquals(
                expected = SearchUseCase.Result.Empty,
                actual = actualResult
            )
        }
    }

    @Test
    fun searchWithEmptyResultsReturnsEmpty(): TestResult {
        val totalPosts = 0
        val resultsPerPage = PostCreator.multiPageMultiPostPageSize()
        val pages: Map<Int, List<Post>> = emptyMap()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                query = SearchQuery,
                searchHistory = mutableMapOf(),
                resultsPerPage = resultsPerPage
            )
            assertEquals(
                expected = SearchUseCase.Result.Empty,
                actual = actualResult,
            )
        }
    }

    @Test
    fun searchWithOneResultReturnsPost(): TestResult {
        val totalPosts = 1
        val resultsPerPage = PostCreator.multiPageMultiPostPageSize()
        val pages = PostCreator.singlePageSinglePost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                query = SearchQuery,
                searchHistory = mutableMapOf(),
                resultsPerPage = resultsPerPage
            )
            assertEquals(
                expected = SearchUseCase.Result.Valid(
                    query = SearchQuery,
                    totalPosts = 1,
                    post = pages[1]!!.first(),
                    postPage = 1,
                    postIndexOnPage = 0,
                    postPageSize = 1
                ),
                actual = actualResult
            )
        }
    }

    @Test
    fun searchExcludes(): TestResult {
        val totalPosts = PostCreator.multiPageMultiPostTotalCount()
        val resultsPerPage = PostCreator.multiPageMultiPostPageSize()
        val pages = PostCreator.multiPageMultiPost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val searchHistory = mutableMapOf<Int, List<Int>>()
            val minPostPage = 1
            val maxPostPage = 2
            val minPostIndexOnPage = 0
            val maxPostIndexOnPage = 3

            for (i in 0 until totalPosts) {
                val actualResult = it.invoke(
                    query = SearchQuery,
                    searchHistory = searchHistory,
                    resultsPerPage = resultsPerPage
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
    }

    @Test
    fun searchExhausts(): TestResult {
        val totalPosts = PostCreator.multiPageMultiPostTotalCount()
        val resultsPerPage = PostCreator.multiPageMultiPostPageSize()
        val pages = PostCreator.multiPageMultiPost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val searchHistory = mutableMapOf<Int, List<Int>>()

            for (i in 0 until totalPosts) {
                val actualResult = it.invoke(
                    query = SearchQuery,
                    searchHistory = searchHistory,
                    resultsPerPage = resultsPerPage
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
                resultsPerPage = PostCreator.multiPageMultiPostPageSize()
            )
            assertTrue { actualResult == SearchUseCase.Result.Exhausted }
        }
    }

    private fun runBlockingTest(
        totalPosts: Int,
        pages: Map<Int, List<Post>>,
        testBlock: suspend (SearchUseCase) -> Unit
    ): TestResult = runTest {
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