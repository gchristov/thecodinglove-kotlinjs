package com.gchristov.thecodinglove.search.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.search.domain.model.SearchStatistics
import com.gchristov.thecodinglove.search.domain.usecase.SearchStatisticsUseCase
import kotlin.test.assertEquals

class FakeSearchStatisticsUseCase(
    private val invocationResult: Either<Throwable, SearchStatistics> = Either.Right(SearchStatisticsCreator.statistics()),
) : SearchStatisticsUseCase {
    private var invocations = 0

    override suspend fun invoke(): Either<Throwable, SearchStatistics> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}

object SearchStatisticsCreator {
    fun statistics(
        messagesSent: Int = 10,
        activeSearchSessions: Int = 3,
        messagesSelfDestruct: Int = 1,
    ) = SearchStatistics(
        messagesSent = messagesSent,
        activeSearchSessions = activeSearchSessions,
        messagesSelfDestruct = messagesSelfDestruct,
    )
}
