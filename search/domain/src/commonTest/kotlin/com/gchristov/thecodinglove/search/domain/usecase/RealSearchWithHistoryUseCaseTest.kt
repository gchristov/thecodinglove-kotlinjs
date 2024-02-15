package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.contains
import com.gchristov.thecodinglove.search.domain.model.insert
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchRepository
import com.gchristov.thecodinglove.search.testfixtures.SearchPostCreator
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
        val pages = SearchPostCreator.multiPageMultiPost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                SearchWithHistoryUseCase.Dto(
                    query = TestSearchQuery,
                    searchHistory = mutableMapOf(),
                )
            )
            assertEquals(
                expected = Either.Left(SearchWithHistoryUseCase.Error.Empty(additionalInfo = "query=$TestSearchQuery")),
                actual = actualResult
            )
        }
    }

    @Test
    fun searchWithEmptyResultsReturnsEmpty(): TestResult {
        val totalPosts = 0
        val pages: Map<Int, List<SearchPost>> = emptyMap()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                SearchWithHistoryUseCase.Dto(
                    query = TestSearchQuery,
                    searchHistory = mutableMapOf(),
                )
            )
            assertEquals(
                expected = Either.Left(SearchWithHistoryUseCase.Error.Empty(additionalInfo = "query=$TestSearchQuery")),
                actual = actualResult,
            )
        }
    }

    @Test
    fun searchWithOneResultReturnsPost(): TestResult {
        val totalPosts = 1
        val pages = SearchPostCreator.singlePageSinglePost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val actualResult = it.invoke(
                SearchWithHistoryUseCase.Dto(
                    query = TestSearchQuery,
                    searchHistory = mutableMapOf(),
                )
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
        val totalPosts = SearchPostCreator.multiPageMultiPostTotalCount()
        val pages = SearchPostCreator.multiPageMultiPost()

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
                    SearchWithHistoryUseCase.Dto(
                        query = TestSearchQuery,
                        searchHistory = searchHistory,
                    )
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
        val totalPosts = SearchPostCreator.multiPageMultiPostTotalCount()
        val pages = SearchPostCreator.multiPageMultiPost()

        return runBlockingTest(
            totalPosts = totalPosts,
            pages = pages
        ) {
            val searchHistory = mutableMapOf<Int, List<Int>>()

            for (i in 0 until totalPosts) {
                val actualResult = (it.invoke(
                    SearchWithHistoryUseCase.Dto(
                        query = TestSearchQuery,
                        searchHistory = searchHistory,
                    )
                ) as Either.Right).value
                searchHistory.insert(
                    postPage = actualResult.postPage,
                    postIndexOnPage = actualResult.postIndexOnPage,
                    currentPageSize = actualResult.postPageSize
                )
            }
            // Make sure we've exhausted all options
            assertTrue { searchHistory.size == SearchPostCreator.multiPageMultiPost().size }
            for (page in SearchPostCreator.multiPageMultiPost().keys) {
                assertTrue {
                    val historyPage = searchHistory[page]!!
                    val testPage = SearchPostCreator.multiPageMultiPost()[page]!!
                    // Fully visited pages have an extra -1 in the list to indicate page termination
                    historyPage.size - 1 == testPage.size
                }
            }
            // If all options are exhausted we shouldn't be able to search for an element
            val actualResult = it.invoke(
                SearchWithHistoryUseCase.Dto(
                    query = TestSearchQuery,
                    searchHistory = searchHistory,
                )
            )
            assertTrue { actualResult == Either.Left(SearchWithHistoryUseCase.Error.Exhausted(additionalInfo = "query=$TestSearchQuery")) }
        }
    }

    private fun runBlockingTest(
        totalPosts: Int,
        pages: Map<Int, List<SearchPost>>,
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
    postsPerPage = SearchPostCreator.multiPageMultiPostPageSize(),
    preloadPubSubTopic = TestSearchPreloadPubSubTopic,
)