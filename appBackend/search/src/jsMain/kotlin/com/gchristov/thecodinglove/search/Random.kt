package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.model.Post
import kotlin.math.max
import kotlin.random.Random

internal fun Random.nextRandomPage(
    totalResults: Int,
    resultsPerPage: Int,
    exclusions: List<Int>
): Either<RangeException, Int> {
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
    exclusions: List<Int>
): Either<RangeException, Int> {
    val min = 0
    val max = max(
        a = min,
        b = posts.size
    )
    if (max == 0) {
        return Either.Left(RangeException.Empty)
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
): Either<RangeException, Int> {
    // Make sure the numbers are sorted
    val sorted = exclusions.sorted()
    val rangeLength = end - start - sorted.size
    if (rangeLength <= 0) {
        return Either.Left(RangeException.Exhausted)
    }
    var randomInt: Int = nextInt(rangeLength) + start
    for (item in sorted) {
        if (item > randomInt) {
            return Either.Right(randomInt)
        }
        randomInt++
    }
    return Either.Right(randomInt)
}

internal sealed class RangeException : Exception() {
    object Empty : RangeException()
    object Exhausted : RangeException()
}