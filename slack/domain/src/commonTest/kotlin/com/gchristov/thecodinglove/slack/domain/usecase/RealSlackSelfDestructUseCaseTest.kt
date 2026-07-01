package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackAuthTokenCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RealSlackSelfDestructUseCaseTest {
    @Test
    fun selfDestructWithTokenDeletesMessageAndState(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
    ) { useCase, repository ->
        val actual = useCase.invoke(
            messageId = TestMessageId,
            userId = TestUserId,
            channelId = TestChannelId,
            messageTs = TestMessageTs,
        )
        assertTrue { actual.isRight() }
        repository.assertDeleteMessageCalled()
        repository.assertDeleteSelfDestructMessageCalledTimes(1)
    }

    @Test
    fun selfDestructWithNoTokenDeletesStateOnly(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Left(Throwable("Token not found")),
    ) { useCase, repository ->
        val actual = useCase.invoke(
            messageId = TestMessageId,
            userId = TestUserId,
            channelId = TestChannelId,
            messageTs = TestMessageTs,
        )
        assertTrue { actual.isRight() }
        repository.assertDeleteMessageNotCalled()
        repository.assertDeleteSelfDestructMessageCalledTimes(1)
    }

    @Test
    fun selfDestructWhenDeleteMessageFailsPropagatesError(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
        deleteMessageResult = Either.Left(Throwable("Delete failed")),
    ) { useCase, repository ->
        val actual = useCase.invoke(
            messageId = TestMessageId,
            userId = TestUserId,
            channelId = TestChannelId,
            messageTs = TestMessageTs,
        )
        assertTrue { actual.isLeft() }
        repository.assertDeleteSelfDestructMessageCalledTimes(0)
    }

    private fun runBlockingTest(
        getAuthTokenResult: Either<Throwable, SlackAuthToken> = Either.Left(Throwable("Token not found")),
        deleteMessageResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackSelfDestructUseCase, FakeSlackRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(
            getAuthTokenResult = getAuthTokenResult,
            deleteMessageResult = deleteMessageResult,
        )
        testBlock(
            RealSlackSelfDestructUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackRepository = repository,
            ),
            repository,
        )
    }
}

private const val TestMessageId = "message_id"
private const val TestUserId = "user_id"
private const val TestChannelId = "channel_id"
private const val TestMessageTs = "message_ts"
