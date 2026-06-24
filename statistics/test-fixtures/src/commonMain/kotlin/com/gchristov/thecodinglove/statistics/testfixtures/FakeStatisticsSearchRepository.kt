package com.gchristov.thecodinglove.statistics.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository
import kotlin.test.assertEquals

class FakeStatisticsSearchRepository(
    private val searchStatisticsResult: Either<Throwable, StatisticsReport.SearchStatistics> = Either.Right(
        StatisticsReportCreator.searchStatistics()
    ),
) : StatisticsSearchRepository {
    private var invocations = 0

    override suspend fun searchStatistics(): Either<Throwable, StatisticsReport.SearchStatistics> {
        invocations++
        return searchStatisticsResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
