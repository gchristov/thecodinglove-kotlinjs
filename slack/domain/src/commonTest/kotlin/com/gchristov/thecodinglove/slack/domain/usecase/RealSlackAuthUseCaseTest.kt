package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackAuthTokenCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealSlackAuthUseCaseTest {
    @Test
    fun authWithNullCodeReturnsCancelled(): TestResult = runBlockingTest { useCase, _ ->
        val actual = useCase.invoke(SlackAuthUseCase.Dto(code = null))
        assertEquals(
            expected = Either.Left(SlackAuthUseCase.Error.Cancelled()),
            actual = actual,
        )
    }

    @Test
    fun authWithEmptyCodeReturnsCancelled(): TestResult = runBlockingTest { useCase, _ ->
        val actual = useCase.invoke(SlackAuthUseCase.Dto(code = ""))
        assertEquals(
            expected = Either.Left(SlackAuthUseCase.Error.Cancelled()),
            actual = actual,
        )
    }

    @Test
    fun authFailsWhenAuthUserFails(): TestResult = runBlockingTest(
        authUserResult = Either.Left(Throwable("Auth failed")),
    ) { useCase, _ ->
        val actual = useCase.invoke(SlackAuthUseCase.Dto(code = TestCode))
        assertTrue { actual.isLeft { it is SlackAuthUseCase.Error.Other } }
    }

    @Test
    fun authFailsWhenSaveTokenFails(): TestResult = runBlockingTest(
        saveAuthTokenResult = Either.Left(Throwable("Save failed")),
    ) { useCase, _ ->
        val actual = useCase.invoke(SlackAuthUseCase.Dto(code = TestCode))
        assertTrue { actual.isLeft { it is SlackAuthUseCase.Error.Other } }
    }

    @Test
    fun authSuccessSavesToken(): TestResult {
        val token = SlackAuthTokenCreator.token()
        return runBlockingTest(
            authUserResult = Either.Right(token),
        ) { useCase, repository ->
            val actual = useCase.invoke(SlackAuthUseCase.Dto(code = TestCode))
            assertEquals(expected = Either.Right(Unit), actual = actual)
            repository.assertAuthTokenSaved(token)
        }
    }

    private fun runBlockingTest(
        authUserResult: Either<Throwable, SlackAuthToken> = Either.Right(SlackAuthTokenCreator.token()),
        saveAuthTokenResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackAuthUseCase, FakeSlackRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(
            authUserResult = authUserResult,
            saveAuthTokenResult = saveAuthTokenResult,
        )
        testBlock(
            RealSlackAuthUseCase(
                dispatcher = FakeCoroutineDispatcher,
                slackConfig = TestSlackConfig,
                log = FakeLogger,
                slackRepository = repository,
            ),
            repository,
        )
    }
}

private const val TestCode = "auth_code_123"
private val TestSlackConfig = SlackConfig(
    signingSecret = "signing_secret",
    timestampValidityMinutes = 5,
    requestVerificationEnabled = true,
    clientId = "client_id",
    clientSecret = "client_secret",
    interactivityReceivedPubSubTopic = "interactivity_topic",
    slashCommandReceivedPubSubTopic = "slash_topic",
)
