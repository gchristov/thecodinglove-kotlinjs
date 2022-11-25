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
        val totalResults = searchRepository.getTotalPosts(query)
        if (totalResults <= 0) {
            return@withContext SearchResult.Empty
        }

        val randomPostPage = Random.nextRandomPage(
            totalResults = totalResults,
            resultsPerPage = resultsPerPage,
            exclusions = searchHistory.getExcludedPages()
        )
        if (randomPostPage == RandomResult.Invalid) {
            return@withContext SearchResult.Empty
        }

        val searchResults = searchRepository.search(
            page = (randomPostPage as RandomResult.Valid).number,
            query = query
        )
        if (searchResults.isEmpty()) {
            return@withContext SearchResult.Empty
        }

        val randomPostIndexOnPage = Random.nextRandomPostIndex(
            posts = searchResults,
            exclusions = searchHistory.getExcludedPostIndexes(randomPostPage.number)
        )
        if (randomPostIndexOnPage == RandomResult.Invalid) {
            return@withContext SearchResult.Empty
        }

        SearchResult.Valid(
            totalPosts = totalResults,
            post = searchResults[(randomPostIndexOnPage as RandomResult.Valid).number],
            postPage = randomPostPage.number,
            postIndexOnPage = randomPostIndexOnPage.number,
            postPageSize = searchResults.size
        )
    }
}

private fun Random.nextRandomPage(
    totalResults: Int,
    resultsPerPage: Int,
    exclusions: Set<Int>
): RandomResult {
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
): RandomResult {
    val min = 0
    val max = max(
        a = min,
        b = posts.size
    )
    if (max == 0) {
        return RandomResult.Invalid
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
    start: Int, // 0
    end: Int, // 4
    exclusions: Set<Int> // [1,2]
): RandomResult {
    // Make sure the numbers are sorted
    val sorted = exclusions.sorted()
    val rangeLength = end - start - sorted.size
    if (rangeLength <= 0) {
        return RandomResult.Invalid
    }
    var randomInt: Int = nextInt(rangeLength) + start
    for (item in sorted) {
        if (item > randomInt) {
            return RandomResult.Valid(randomInt)
        }
        randomInt++
    }
    return RandomResult.Valid(randomInt)
}

private sealed class RandomResult {
    object Invalid : RandomResult()
    data class Valid(val number: Int) : RandomResult()
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