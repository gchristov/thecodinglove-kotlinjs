package com.gchristov.thecodinglove.search.domain

import arrow.core.Either
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import kotlin.math.max
import kotlin.random.Random

internal fun Random.nextRandomPage(
    totalResults: Int,
    resultsPerPage: Int,
    exclusions: List<Int>
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

internal fun Random.nextRandomPostIndex(
    posts: List<SearchPost>,
    exclusions: List<Int>
): RandomResult {
    val min = 0
    val max = max(
        a = min,
        b = posts.size
    )
    if (max == 0) {
        return RandomResult.Empty
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
    exclusions: List<Int>
): RandomResult {
    // Make sure the numbers are sorted
    val sorted = exclusions.sorted()
    val rangeLength = end - start - sorted.size
    if (rangeLength <= 0) {
        return RandomResult.Exhausted
    }
    var randomInt: Int = nextInt(rangeLength) + start
    for (item in sorted) {
        if (item > randomInt) {
            return RandomResult.Data(randomInt)
        }
        randomInt++
    }
    return RandomResult.Data(randomInt)
}

internal sealed class RandomResult {
    object Empty : RandomResult()
    object Exhausted : RandomResult()
    data class Data(val result: Int) : RandomResult()
}