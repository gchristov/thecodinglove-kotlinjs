package com.gchristov.thecodinglove.statistics.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.usecase.StatisticsReportUseCase
import kotlin.test.assertEquals

class FakeStatisticsReportUseCase(
    private val invocationResult: Either<Throwable, StatisticsReport> = Either.Right(
        StatisticsReport(
            searchStatistics = StatisticsReport.SearchStatistics(
                messagesSent = 0,
                activeSearchSessions = 0,
                messagesSelfDestruct = 0,
            ),
            slackStatistics = StatisticsReport.SlackStatistics(
                activeSelfDestructMessages = 0,
                users = 0,
                teams = 0,
            ),
        )
    ),
) : StatisticsReportUseCase {
    private var invocations = 0

    override suspend fun invoke(): Either<Throwable, StatisticsReport> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
