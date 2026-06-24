package com.gchristov.thecodinglove.statistics.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.testfixtures.FakeStatisticsSearchRepository
import com.gchristov.thecodinglove.statistics.testfixtures.FakeStatisticsSlackRepository
import com.gchristov.thecodinglove.statistics.testfixtures.StatisticsReportCreator
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
        searchStatisticsResult = Either.Right(StatisticsReportCreator.searchStatistics()),
        slackStatisticsResult = Either.Right(StatisticsReportCreator.slackStatistics()),
    ) { useCase ->
        assertEquals(
            expected = Either.Right(StatisticsReportCreator.report()),
            actual = useCase.invoke(),
        )
    }

    private fun runBlockingTest(
        searchStatisticsResult: Either<Throwable, StatisticsReport.SearchStatistics> = Either.Right(StatisticsReportCreator.searchStatistics()),
        slackStatisticsResult: Either<Throwable, StatisticsReport.SlackStatistics> = Either.Right(StatisticsReportCreator.slackStatistics()),
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
