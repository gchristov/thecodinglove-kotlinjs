package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.model.isSelfDestruct
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackAuthTokenCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSearchSessionPostCreator
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTime::class)
class RealSlackSendSearchUseCaseTest {
    @Test
    fun sendWithNoAuthTokenReturnsNotAuthenticatedError(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Left(Throwable("No token")),
    ) { useCase, repository, searchRepository ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isLeft() }
        assertEquals(SlackSendSearchUseCase.Error.NotAuthenticated, actual.leftOrNull())
        repository.assertPostMessageToUrlCalledTimes(0)
        searchRepository.assertGetSessionPostNotInvoked()
    }

    @Test
    fun sendFailsWhenGetSessionPostFails(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
        getSearchSessionPostResult = Either.Left(Throwable("Session not found")),
    ) { useCase, _, _ ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isLeft() }
    }

    @Test
    fun sendFailsWhenPostCancelMessageFails(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
        postMessageToUrlResult = Either.Left(Throwable("Post failed")),
    ) { useCase, _, _ ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isLeft() }
    }

    @Test
    fun sendFailsWhenPostMessageFails(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
        postMessageResult = Either.Left(Throwable("Post failed")),
    ) { useCase, _, _ ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isLeft() }
    }

    @Test
    fun sendFailsWhenUpdateStateFails(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
        updateSearchSessionStateResult = Either.Left(Throwable("Update failed")),
    ) { useCase, _, _ ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isLeft() }
    }

    @Test
    fun sendSucceedsWithoutSelfDestruct(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
    ) { useCase, repository, searchRepository ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isRight() }
        val message = actual.getOrElse { null }
        assertNotNull(message)
        assertFalse { message.isSelfDestruct }
        repository.assertPostMessageToUrlCalledTimes(1)
        repository.assertPostMessageCalled()
        searchRepository.assertGetSessionPostInvokedOnce()
        searchRepository.assertUpdateStateCalledOnce()
        repository.assertSelfDestructMessageNotSaved()
    }

    @Test
    fun sendSuccessWithSelfDestructSavesSelfDestructStateAndReturnsIt(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
    ) { useCase, repository, _ ->
        val actual = useCase.invoke(TestDto.copy(selfDestructMinutes = 5))
        assertTrue { actual.isRight() }
        val message = actual.getOrElse { null }
        assertNotNull(message)
        assertTrue { message.isSelfDestruct }
        repository.assertSelfDestructMessageSaved()
    }

    private fun runBlockingTest(
        getAuthTokenResult: Either<Throwable, SlackAuthToken> = Either.Left(Throwable("No token")),
        getSearchSessionPostResult: Either<Throwable, SlackSearchRepository.SearchSessionPostDto> = Either.Right(SlackSearchSessionPostCreator.post()),
        postMessageToUrlResult: Either<Throwable, Unit> = Either.Right(Unit),
        postMessageResult: Either<Throwable, String> = Either.Right("message_ts"),
        updateSearchSessionStateResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackSendSearchUseCase, FakeSlackRepository, FakeSlackSearchRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(
            getAuthTokenResult = getAuthTokenResult,
            postMessageToUrlResult = postMessageToUrlResult,
            postMessageResult = postMessageResult,
        )
        val searchRepository = FakeSlackSearchRepository(
            getSearchSessionPostResult = getSearchSessionPostResult,
            updateSearchSessionStateResult = updateSearchSessionStateResult,
        )
        testBlock(
            RealSlackSendSearchUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackSearchRepository = searchRepository,
                slackRepository = repository,
                slackMessageFactory = FakeSlackMessageFactory(),
                clock = TestClock,
            ),
            repository,
            searchRepository,
        )
    }
}

private val TestDto = SlackSendSearchUseCase.Dto(
    userId = "user_id",
    teamId = "team_id",
    channelId = "channel_id",
    responseUrl = "https://response.url",
    searchSessionId = "session_123",
    selfDestructMinutes = null,
)
@OptIn(ExperimentalTime::class)
private val TestClock = object : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(1000L)
}
