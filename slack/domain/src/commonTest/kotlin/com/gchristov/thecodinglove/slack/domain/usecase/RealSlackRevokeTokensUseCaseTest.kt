package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RealSlackRevokeTokensUseCaseTest {
    @Test
    fun emptyListsDeleteNothing(): TestResult = runBlockingTest { useCase, repository ->
        val actual = useCase.invoke(SlackRevokeTokensUseCase.Dto(oAuth = null, bot = null))
        assertTrue { actual.isRight() }
        repository.assertDeleteAuthTokenCalledTimes(0)
    }

    @Test
    fun oauthTokensAreDeleted(): TestResult = runBlockingTest { useCase, repository ->
        val actual = useCase.invoke(SlackRevokeTokensUseCase.Dto(
            oAuth = listOf("token_1", "token_2"),
            bot = null,
        ))
        assertTrue { actual.isRight() }
        repository.assertDeleteAuthTokenCalledTimes(2)
    }

    @Test
    fun botTokensAreDeleted(): TestResult = runBlockingTest { useCase, repository ->
        val actual = useCase.invoke(SlackRevokeTokensUseCase.Dto(
            oAuth = null,
            bot = listOf("bot_1"),
        ))
        assertTrue { actual.isRight() }
        repository.assertDeleteAuthTokenCalledTimes(1)
    }

    @Test
    fun bothOauthAndBotTokensAreDeleted(): TestResult = runBlockingTest { useCase, repository ->
        val actual = useCase.invoke(SlackRevokeTokensUseCase.Dto(
            oAuth = listOf("token_1"),
            bot = listOf("bot_1", "bot_2"),
        ))
        assertTrue { actual.isRight() }
        repository.assertDeleteAuthTokenCalledTimes(3)
    }

    @Test
    fun deleteFailurePropagates(): TestResult = runBlockingTest(
        deleteAuthTokenResult = Either.Left(Throwable("Delete failed")),
    ) { useCase, _ ->
        val actual = useCase.invoke(SlackRevokeTokensUseCase.Dto(
            oAuth = listOf("token_1"),
            bot = null,
        ))
        assertTrue { actual.isLeft() }
    }

    private fun runBlockingTest(
        deleteAuthTokenResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackRevokeTokensUseCase, FakeSlackRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(deleteAuthTokenResult = deleteAuthTokenResult)
        testBlock(
            RealSlackRevokeTokensUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackRepository = repository,
            ),
            repository,
        )
    }
}
