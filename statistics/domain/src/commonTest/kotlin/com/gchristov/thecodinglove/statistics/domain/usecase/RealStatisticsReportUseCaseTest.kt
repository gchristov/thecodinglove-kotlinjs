package com.gchristov.thecodinglove.statistics.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.testfixtures.FakeStatisticsSearchRepository
import com.gchristov.thecodinglove.statistics.testfixtures.FakeStatisticsSlackRepository
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealStatisticsReportUseCaseTest {
    @Test
    fun reportFailsWhenSearchStatisticsFails(): TestResult = runBlockingTest(
        searchStatisticsResult = Either.Left(Throwable("Search DB error")),
    ) { useCase ->
        assertTrue { useCase.invoke().isLeft() }
    }

    @Test
    fun reportFailsWhenSlackStatisticsFails(): TestResult = runBlockingTest(
        slackStatisticsResult = Either.Left(Throwable("Slack DB error")),
    ) { useCase ->
        assertTrue { useCase.invoke().isLeft() }
    }

    @Test
    fun reportCombinesBothSources(): TestResult = runBlockingTest(
        searchStatisticsResult = Either.Right(TestSearchStatistics),
        slackStatisticsResult = Either.Right(TestSlackStatistics),
    ) { useCase ->
        assertEquals(
            expected = Either.Right(StatisticsReport(
                searchStatistics = TestSearchStatistics,
                slackStatistics = TestSlackStatistics,
            )),
            actual = useCase.invoke(),
        )
    }

    private fun runBlockingTest(
        searchStatisticsResult: Either<Throwable, StatisticsReport.SearchStatistics> = Either.Right(TestSearchStatistics),
        slackStatisticsResult: Either<Throwable, StatisticsReport.SlackStatistics> = Either.Right(TestSlackStatistics),
        testBlock: suspend (StatisticsReportUseCase) -> Unit,
    ): TestResult = runTest {
        testBlock(
            RealStatisticsReportUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                statisticsSearchRepository = FakeStatisticsSearchRepository(searchStatisticsResult),
                statisticsSlackRepository = FakeStatisticsSlackRepository(slackStatisticsResult),
            )
        )
    }
}

private val TestSearchStatistics = StatisticsReport.SearchStatistics(
    messagesSent = 100,
    activeSearchSessions = 5,
    messagesSelfDestruct = 3,
)

private val TestSlackStatistics = StatisticsReport.SlackStatistics(
    activeSelfDestructMessages = 2,
    users = 50,
    teams = 10,
)
