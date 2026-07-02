package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackAuthTokenCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealSlackEnsureAuthenticatedUseCaseTest {
    @Test
    fun ensureAuthenticatedWithValidTokenReturnsAuthenticated(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
    ) { useCase, repository ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isRight() }
        assertEquals(SlackEnsureAuthenticatedUseCase.Result.Authenticated, actual.getOrElse { null })
        repository.assertPostMessageToUrlCalledTimes(0)
    }

    @Test
    fun ensureAuthenticatedWithNoTokenPostsAuthPromptAndReturnsAuthenticationPromptSent(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Left(Throwable("No token")),
    ) { useCase, repository ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isRight() }
        assertEquals(SlackEnsureAuthenticatedUseCase.Result.AuthenticationPromptSent, actual.getOrElse { null })
        repository.assertPostMessageToUrlCalledTimes(1)
    }

    @Test
    fun ensureAuthenticatedWithNoTokenAndPromptPostFailureReturnsLeft(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Left(Throwable("No token")),
        postMessageToUrlResult = Either.Left(Throwable("Post failed")),
    ) { useCase, _ ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isLeft() }
    }

    private fun runBlockingTest(
        getAuthTokenResult: Either<Throwable, SlackAuthToken> = Either.Left(Throwable("No token")),
        postMessageToUrlResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackEnsureAuthenticatedUseCase, FakeSlackRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(
            getAuthTokenResult = getAuthTokenResult,
            postMessageToUrlResult = postMessageToUrlResult,
        )
        testBlock(
            RealSlackEnsureAuthenticatedUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackRepository = repository,
                slackMessageFactory = FakeSlackMessageFactory(),
                slackConfig = SlackConfigCreator.slackConfig(),
            ),
            repository,
        )
    }
}

private val TestDto = SlackEnsureAuthenticatedUseCase.Dto(
    userId = "user_id",
    teamId = "team_id",
    channelId = "channel_id",
    responseUrl = "https://response.url",
    searchSessionId = "session_123",
    selfDestructSeconds = null,
)
