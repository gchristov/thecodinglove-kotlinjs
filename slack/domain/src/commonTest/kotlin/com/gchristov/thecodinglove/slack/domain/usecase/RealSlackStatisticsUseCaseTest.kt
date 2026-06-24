package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackStatistics
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackAuthTokenCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSelfDestructMessageCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealSlackStatisticsUseCaseTest {
    @Test
    fun statisticsFailsWhenGetSelfDestructMessagesFails(): TestResult = runBlockingTest(
        getSelfDestructMessagesResult = Either.Left(Throwable("DB error")),
    ) { useCase ->
        val actual = useCase.invoke()
        assertTrue { actual.isLeft() }
    }

    @Test
    fun statisticsFailsWhenGetAuthTokensFails(): TestResult = runBlockingTest(
        getAuthTokensResult = Either.Left(Throwable("DB error")),
    ) { useCase ->
        val actual = useCase.invoke()
        assertTrue { actual.isLeft() }
    }

    @Test
    fun statisticsCountsCorrectly(): TestResult = runBlockingTest(
        getSelfDestructMessagesResult = Either.Right(listOf(
            SlackSelfDestructMessageCreator.pastMessage(),
            SlackSelfDestructMessageCreator.futureMessage(),
        )),
        getAuthTokensResult = Either.Right(listOf(
            SlackAuthTokenCreator.token(id = "user_1"),
            SlackAuthTokenCreator.token(id = "user_2"),
        )),
    ) { useCase ->
        val actual = useCase.invoke()
        assertEquals(
            expected = Either.Right(SlackStatistics(
                activeSelfDestructMessages = 2,
                users = 2,
                teams = 1,
            )),
            actual = actual,
        )
    }

    @Test
    fun statisticsCountsDistinctTeams(): TestResult = runBlockingTest(
        getSelfDestructMessagesResult = Either.Right(emptyList()),
        getAuthTokensResult = Either.Right(listOf(
            SlackAuthToken(id = "user_1", scope = "scope", token = "token", teamId = "team_a", teamName = "Team A"),
            SlackAuthToken(id = "user_2", scope = "scope", token = "token", teamId = "team_b", teamName = "Team B"),
            SlackAuthToken(id = "user_3", scope = "scope", token = "token", teamId = "team_a", teamName = "Team A"),
        )),
    ) { useCase ->
        val actual = useCase.invoke()
        assertEquals(
            expected = Either.Right(SlackStatistics(
                activeSelfDestructMessages = 0,
                users = 3,
                teams = 2,
            )),
            actual = actual,
        )
    }

    private fun runBlockingTest(
        getSelfDestructMessagesResult: Either<Throwable, List<SlackSelfDestructMessage>> = Either.Right(emptyList()),
        getAuthTokensResult: Either<Throwable, List<SlackAuthToken>> = Either.Right(emptyList()),
        testBlock: suspend (SlackStatisticsUseCase) -> Unit,
    ): TestResult = runTest {
        testBlock(
            RealSlackStatisticsUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackRepository = FakeSlackRepository(
                    getSelfDestructMessagesResult = getSelfDestructMessagesResult,
                    getAuthTokensResult = getAuthTokensResult,
                ),
            )
        )
    }
}
