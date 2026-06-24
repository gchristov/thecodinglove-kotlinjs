package com.gchristov.thecodinglove.statistics.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.usecase.StatisticsReportUseCase
import kotlin.test.assertEquals

class FakeStatisticsReportUseCase(
    private val invocationResult: Either<Throwable, StatisticsReport> = Either.Right(
        StatisticsReportCreator.report()
    ),
) : StatisticsReportUseCase {
    private var invocations = 0

    override suspend fun invoke(): Either<Throwable, StatisticsReport> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
