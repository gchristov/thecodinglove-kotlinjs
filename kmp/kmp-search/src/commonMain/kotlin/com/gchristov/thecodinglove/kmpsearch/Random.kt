package com.gchristov.thecodinglove.kmpsearch

import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import kotlin.math.max
import kotlin.random.Random

internal fun Random.nextRandomPage(
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

internal fun Random.nextRandomPostIndex(
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

internal sealed class RandomResult {
    object Invalid : RandomResult()
    data class Valid(val number: Int) : RandomResult()
}