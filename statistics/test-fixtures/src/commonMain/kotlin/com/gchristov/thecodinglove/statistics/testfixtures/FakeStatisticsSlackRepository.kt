package com.gchristov.thecodinglove.statistics.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository
import kotlin.test.assertEquals

class FakeStatisticsSlackRepository(
    private val slackStatisticsResult: Either<Throwable, StatisticsReport.SlackStatistics> = Either.Right(
        StatisticsReport.SlackStatistics(activeSelfDestructMessages = 0, users = 0, teams = 0)
    ),
) : StatisticsSlackRepository {
    private var invocations = 0

    override suspend fun slackStatistics(): Either<Throwable, StatisticsReport.SlackStatistics> {
        invocations++
        return slackStatisticsResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
