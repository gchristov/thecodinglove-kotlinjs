package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpsearchdata.Post
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.random.Random

class SearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(
        query: String,
        searchHistory: SearchHistory,
        resultsPerPage: Int
    ): SearchResult = withContext(dispatcher) {
        // Obtain total number of posts
        val totalResults = searchRepository.getTotalPosts(query)
        if (totalResults <= 0) {
            return@withContext SearchResult.Empty
        }
        // Randomise selected page
        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = resultsPerPage,
            exclusions = searchHistory.getExcludedPages()
        )
        val searchResults = searchRepository.search(
            page = randomPostPage,
            query = query
        )
        if (searchResults.isEmpty()) {
            return@withContext SearchResult.Empty
        }
        // Randomise selected post from the search results
        val randomPostIndexOnPage = Random.nextRandomPostIndex(
            posts = searchResults,
            exclusions = searchHistory.getExcludedPostIndexes(randomPostPage)
        )
        SearchResult.Valid(
            totalPosts = totalResults,
            post = searchResults[randomPostIndexOnPage],
            postPage = randomPostPage,
            postIndexOnPage = randomPostIndexOnPage,
            postPageSize = searchResults.size
        )
    }
}

private fun Random.nextRandomPage(
    totalResults: Int,
    resultsPerPage: Int,
    exclusions: Set<Int>
): Int {
    val min = 1
    val max = max(
        a = min,
        b = totalResults / resultsPerPage + if (totalResults % resultsPerPage > 0) 1 else 0
    )
    return nextRandomIntInRange(
        start = min,
        end = max + 1,
        exclusions = exclusions
    )
}

private fun Random.nextRandomPostIndex(
    posts: List<Post>,
    exclusions: Set<Int>
): Int {
    val min = 0
    val max = max(
        a = min,
        b = posts.size
    )
    if (max == 0) {
        throw UnsupportedOperationException("List of posts cannot be empty")
    }
    return nextRandomIntInRange(
        start = min,
        end = max,
        exclusions = exclusions
    )
}

/**
 * @param start start of range (inclusive)
 * @param end end of range (exclusive)
 * @param exclusions numbers to exclude (= numbers you do not want)
 * @return A random number within start-end, making sure it's not present in [exclusions]
 */
private fun Random.nextRandomIntInRange(
    start: Int,
    end: Int,
    exclusions: Set<Int>
): Int {
    // Make sure the numbers are sorted
    val sorted = exclusions.sorted()
    val rangeLength = end - start - sorted.size
    if (rangeLength <= 0) {
        return end - 1
    }
    var randomInt: Int = nextInt(rangeLength) + start
    for (item in sorted) {
        if (item > randomInt) {
            return randomInt
        }
        randomInt++
    }
    return randomInt
}

sealed class SearchResult {
    object Empty : SearchResult()
    data class Valid(
        val totalPosts: Int,
        val post: Post,
        val postPage: Int,
        val postIndexOnPage: Int,
        val postPageSize: Int
    ) : SearchResult()
}