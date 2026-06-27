package com.gchristov.thecodinglove.search.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchHistoryTest {
    @Test
    fun insertAddsPostIndexToNewPage() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 0, currentPageSize = 3)
        assertEquals(expected = listOf(0), actual = history[1])
    }

    @Test
    fun insertAddsPostIndexToExistingPage() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 0, currentPageSize = 3)
        history.insert(postPage = 1, postIndexOnPage = 1, currentPageSize = 3)
        assertEquals(expected = listOf(0, 1), actual = history[1])
    }

    @Test
    fun insertAddsTerminationIndexWhenPageIsFull() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 0, currentPageSize = 1)
        assertEquals(expected = listOf(0, -1), actual = history[1])
    }

    @Test
    fun containsReturnsTrueForInsertedEntry() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 2, currentPageSize = 3)
        assertTrue { history.contains(postPage = 1, postIndexOnPage = 2) }
    }

    @Test
    fun containsReturnsFalseForMissingPage() {
        val history = mutableMapOf<Int, List<Int>>()
        assertFalse { history.contains(postPage = 1, postIndexOnPage = 0) }
    }

    @Test
    fun containsReturnsFalseForDifferentPage() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 0, currentPageSize = 3)
        assertFalse { history.contains(postPage = 2, postIndexOnPage = 0) }
    }

    @Test
    fun getExcludedPagesReturnsOnlyFullPages() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 0, currentPageSize = 1) // full
        history.insert(postPage = 2, postIndexOnPage = 0, currentPageSize = 3) // not full
        assertEquals(expected = listOf(1), actual = history.getExcludedPages())
    }

    @Test
    fun getExcludedPagesReturnsEmptyWhenNoPagesAreFull() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 0, currentPageSize = 3)
        assertTrue { history.getExcludedPages().isEmpty() }
    }

    @Test
    fun getExcludedPostIndexesReturnsIndexesForKnownPage() {
        val history = mutableMapOf<Int, List<Int>>()
        history.insert(postPage = 1, postIndexOnPage = 0, currentPageSize = 3)
        history.insert(postPage = 1, postIndexOnPage = 1, currentPageSize = 3)
        assertEquals(expected = listOf(0, 1), actual = history.getExcludedPostIndexes(page = 1))
    }

    @Test
    fun getExcludedPostIndexesReturnsEmptyForUnknownPage() {
        val history = mutableMapOf<Int, List<Int>>()
        assertTrue { history.getExcludedPostIndexes(page = 99).isEmpty() }
    }
}
