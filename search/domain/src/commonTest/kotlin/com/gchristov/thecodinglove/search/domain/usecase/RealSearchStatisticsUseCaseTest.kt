package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.domain.model.SearchStatistics
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchRepository
import com.gchristov.thecodinglove.search.testfixtures.SearchSessionCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealSearchStatisticsUseCaseTest {
    @Test
    fun statisticsFailsWhenSessionQueryFails(): TestResult = runBlockingTest(
        findSessionsResult = Either.Left(Throwable("DB error")),
    ) { useCase ->
        assertTrue { useCase.invoke().isLeft() }
    }

    @Test
    fun statisticsReturnsCountsFromRepository(): TestResult = runBlockingTest(
        findSessionsResult = Either.Right(listOf(
            SearchSessionCreator.searchSession(id = "s1", query = "q"),
            SearchSessionCreator.searchSession(id = "s2", query = "q"),
        )),
    ) { useCase ->
        assertEquals(
            expected = Either.Right(SearchStatistics(
                messagesSent = 2,
                activeSearchSessions = 2,
                messagesSelfDestruct = 2,
            )),
            actual = useCase.invoke(),
        )
    }

    private fun runBlockingTest(
        findSessionsResult: Either<Throwable, List<com.gchristov.thecodinglove.search.domain.model.SearchSession>> = Either.Right(emptyList()),
        testBlock: suspend (SearchStatisticsUseCase) -> Unit,
    ): TestResult = runTest {
        testBlock(
            RealSearchStatisticsUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                searchRepository = FakeSearchRepository(
                    findSearchSessionsResult = findSessionsResult,
                ),
            )
        )
    }
}
