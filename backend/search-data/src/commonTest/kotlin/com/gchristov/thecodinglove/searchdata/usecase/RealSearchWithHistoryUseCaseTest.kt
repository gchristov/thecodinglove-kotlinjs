package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.commontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.searchdata.domain.Post
import com.gchristov.thecodinglove.searchdata.domain.SearchConfig
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchtestfixtures.FakeSearchRepository
import com.gchristov.thecodinglove.searchtestfixtures.PostCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RealSearchWithHistoryUseCaseTest {
    @Test
    fun searchWithNoResultsReturnsEmpty(): TestResult {
        val totalPosts = 0
        val pages = PostCreator.multiPageMultiPost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                query = TestSearchQuery,
                searchHistory = mutableMapOf(),
            )
            assertEquals(
                expected = Either.Left(SearchError.Empty),
                actual = actualResult
            )
        }
    }

    @Test
    fun searchWithEmptyResultsReturnsEmpty(): TestResult {
        val totalPosts = 0
        val pages: Map<Int, List<Post>> = emptyMap()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                query = TestSearchQuery,
                searchHistory = mutableMapOf(),
            )
            assertEquals(
                expected = Either.Left(SearchError.Empty),
                actual = actualResult,
            )
        }
    }

    @Test
    fun searchWithOneResultReturnsPost(): TestResult {
        val totalPosts = 1
        val pages = PostCreator.singlePageSinglePost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                query = TestSearchQuery,
                searchHistory = mutableMapOf(),
            )
            assertEquals(
                expected = Either.Right(
                    SearchWithHistoryUseCase.Result(
                        query = TestSearchQuery,
                        totalPosts = 1,
                        post = pages[1]!!.first(),
                        postPage = 1,
                        postIndexOnPage = 0,
                        postPageSize = 1
                    )
                ),
                actual = actualResult
            )
        }
    }

    @Test
    fun searchExcludes(): TestResult {
        val totalPosts = PostCreator.multiPageMultiPostTotalCount()
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
                val actualResult = (it.invoke(
                    query = TestSearchQuery,
                    searchHistory = searchHistory,
                ) as Either.Right).value
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
        val pages = PostCreator.multiPageMultiPost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val searchHistory = mutableMapOf<Int, List<Int>>()

            for (i in 0 until totalPosts) {
                val actualResult = (it.invoke(
                    query = TestSearchQuery,
                    searchHistory = searchHistory,
                ) as Either.Right).value
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
                    // Fully visited pages have an extra -1 in the list to indicate page termination
                    historyPage.size - 1 == testPage.size
                }
            }
            // If all options are exhausted we shouldn't be able to search for an element
            val actualResult = it.invoke(
                query = TestSearchQuery,
                searchHistory = searchHistory,
            )
            assertTrue { actualResult == Either.Left(SearchError.Exhausted) }
        }
    }

    private fun runBlockingTest(
        totalPosts: Int,
        pages: Map<Int, List<Post>>,
        testBlock: suspend (SearchWithHistoryUseCase) -> Unit
    ): TestResult = runTest {
        val searchRepository = FakeSearchRepository(
            totalPosts = totalPosts,
            pages = pages
        )
        val useCase = RealSearchWithHistoryUseCase(
            dispatcher = FakeCoroutineDispatcher,
            searchRepository = searchRepository,
            searchConfig = TestSearchConfig
        )
        testBlock(useCase)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchPreloadPubSubTopic = "topic_123"
private val TestSearchConfig = SearchConfig(
    postsPerPage = PostCreator.multiPageMultiPostPageSize(),
    preloadPubSubTopic = TestSearchPreloadPubSubTopic,
)