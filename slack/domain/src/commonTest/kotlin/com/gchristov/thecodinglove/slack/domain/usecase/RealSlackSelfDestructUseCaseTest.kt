package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackAuthTokenCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSelfDestructMessageCreator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTime::class)
class RealSlackSelfDestructUseCaseTest {
    @Test
    fun selfDestructWithNoMessagesSucceeds(): TestResult = runBlockingTest(
        getSelfDestructMessagesResult = Either.Right(emptyList()),
    ) { useCase, repository ->
        val actual = useCase.invoke()
        assertTrue { actual.isRight() }
        repository.assertDeleteSelfDestructMessageCalledTimes(0)
        repository.assertDeleteMessageNotCalled()
    }

    @Test
    fun selfDestructWithFutureMessageDoesNotDestroy(): TestResult = runBlockingTest(
        getSelfDestructMessagesResult = Either.Right(listOf(SlackSelfDestructMessageCreator.futureMessage())),
    ) { useCase, repository ->
        val actual = useCase.invoke()
        assertTrue { actual.isRight() }
        repository.assertDeleteSelfDestructMessageCalledTimes(0)
        repository.assertDeleteMessageNotCalled()
    }

    @Test
    fun selfDestructWithPastMessageAndTokenDeletesMessageAndState(): TestResult = runBlockingTest(
        getSelfDestructMessagesResult = Either.Right(listOf(SlackSelfDestructMessageCreator.pastMessage())),
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
    ) { useCase, repository ->
        val actual = useCase.invoke()
        assertTrue { actual.isRight() }
        repository.assertDeleteMessageCalled()
        repository.assertDeleteSelfDestructMessageCalledTimes(1)
    }

    @Test
    fun selfDestructWithPastMessageAndNoTokenDeletesStateOnly(): TestResult = runBlockingTest(
        getSelfDestructMessagesResult = Either.Right(listOf(SlackSelfDestructMessageCreator.pastMessage())),
        getAuthTokenResult = Either.Left(Throwable("Token not found")),
    ) { useCase, repository ->
        val actual = useCase.invoke()
        assertTrue { actual.isRight() }
        repository.assertDeleteMessageNotCalled()
        repository.assertDeleteSelfDestructMessageCalledTimes(1)
    }

    private fun runBlockingTest(
        getSelfDestructMessagesResult: Either<Throwable, List<SlackSelfDestructMessage>> = Either.Right(emptyList()),
        getAuthTokenResult: Either<Throwable, SlackAuthToken> = Either.Left(Throwable("Token not found")),
        testBlock: suspend (SlackSelfDestructUseCase, FakeSlackRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(
            getSelfDestructMessagesResult = getSelfDestructMessagesResult,
            getAuthTokenResult = getAuthTokenResult,
        )
        testBlock(
            RealSlackSelfDestructUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackRepository = repository,
                clock = TestClock,
            ),
            repository,
        )
    }
}

@OptIn(ExperimentalTime::class)
private val TestClock = object : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(1000L)
}
