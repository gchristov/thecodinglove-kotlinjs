package com.gchristov.thecodinglove.search.domain

import arrow.core.Either
import com.gchristov.thecodinglove.search.testfixtures.SearchPostCreator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RandomTest {
    @Test
    fun nextRandomPageWithSinglePageAndNoExclusionsReturnsPage1() {
        val result = Random.nextRandomPage(
            totalResults = 1,
            resultsPerPage = 1,
            exclusions = emptyList(),
        )
        assertEquals(expected = Either.Right(1), actual = result)
    }

    @Test
    fun nextRandomPageWithAllButOnePageExcludedReturnsThatPage() {
        val result = Random.nextRandomPage(
            totalResults = 2,
            resultsPerPage = 1,
            exclusions = listOf(1),
        )
        assertEquals(expected = Either.Right(2), actual = result)
    }

    @Test
    fun nextRandomPageWhenAllPagesExcludedReturnsExhausted() {
        val result = Random.nextRandomPage(
            totalResults = 1,
            resultsPerPage = 1,
            exclusions = listOf(1),
        )
        assertEquals(expected = Either.Left(RangeError.Exhausted), actual = result)
    }

    @Test
    fun nextRandomPageCalculatesCorrectPageCountWithRemainder() {
        // 5 results at 2 per page = 3 pages; excluding 1 and 2 leaves only page 3
        val result = Random.nextRandomPage(
            totalResults = 5,
            resultsPerPage = 2,
            exclusions = listOf(1, 2),
        )
        assertEquals(expected = Either.Right(3), actual = result)
    }

    @Test
    fun nextRandomPageCalculatesCorrectPageCountWithoutRemainder() {
        // 4 results at 2 per page = 2 pages; excluding page 1 leaves only page 2
        val result = Random.nextRandomPage(
            totalResults = 4,
            resultsPerPage = 2,
            exclusions = listOf(1),
        )
        assertEquals(expected = Either.Right(2), actual = result)
    }

    @Test
    fun nextRandomPostIndexWithEmptyPostsReturnsEmpty() {
        val result = Random.nextRandomPostIndex(
            posts = emptyList(),
            exclusions = emptyList(),
        )
        assertEquals(expected = Either.Left(RangeError.Empty), actual = result)
    }

    @Test
    fun nextRandomPostIndexWithSinglePostAndNoExclusionsReturnsIndex0() {
        val result = Random.nextRandomPostIndex(
            posts = SearchPostCreator.singlePageSinglePost()[1]!!,
            exclusions = emptyList(),
        )
        assertEquals(expected = Either.Right(0), actual = result)
    }

    @Test
    fun nextRandomPostIndexWithAllButLastIndexExcludedReturnsLastIndex() {
        // 4 posts (indices 0-3); exclude 0, 1, 2 — only index 3 remains
        val posts = SearchPostCreator.multiPageMultiPost()[1]!!
        val result = Random.nextRandomPostIndex(
            posts = posts,
            exclusions = listOf(0, 1, 2),
        )
        assertEquals(expected = Either.Right(3), actual = result)
    }

    @Test
    fun nextRandomPostIndexWhenAllIndexesExcludedReturnsExhausted() {
        val result = Random.nextRandomPostIndex(
            posts = SearchPostCreator.singlePageSinglePost()[1]!!,
            exclusions = listOf(0),
        )
        assertEquals(expected = Either.Left(RangeError.Exhausted), actual = result)
    }
}
