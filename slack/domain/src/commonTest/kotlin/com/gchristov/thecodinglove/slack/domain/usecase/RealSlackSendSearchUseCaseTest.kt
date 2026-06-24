package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackAuthTokenCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSearchSessionPostCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertTrue

class RealSlackSendSearchUseCaseTest {
    @Test
    fun sendWithNoAuthTokenPostsAuthMessage(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Left(Throwable("No token")),
    ) { useCase, repository, searchRepository ->
        val actual = useCase.invoke(TestDto)
        assertTrue { actual.isRight() }
        repository.assertPostMessageToUrlCalledTimes(1)
        repository.assertPostMessageNotCalled()
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
        repository.assertPostMessageToUrlCalledTimes(1)
        repository.assertPostMessageCalled()
        searchRepository.assertGetSessionPostInvokedOnce()
        searchRepository.assertUpdateStateCalledOnce()
        repository.assertSelfDestructMessageNotSaved()
    }

    @Test
    fun sendSuccessWithSelfDestructSavesSelfDestructState(): TestResult = runBlockingTest(
        getAuthTokenResult = Either.Right(SlackAuthTokenCreator.token()),
    ) { useCase, repository, _ ->
        val actual = useCase.invoke(TestDto.copy(selfDestructMinutes = 5))
        assertTrue { actual.isRight() }
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
                slackConfig = TestSlackConfig,
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
private val TestSlackConfig = SlackConfig(
    signingSecret = "signing_secret",
    timestampValidityMinutes = 5,
    requestVerificationEnabled = true,
    clientId = "client_id",
    clientSecret = "client_secret",
    interactivityPubSubTopic = "interactivity_topic",
    slashCommandPubSubTopic = "slash_topic",
)
private val TestClock = object : Clock {
    override fun now(): Instant = Instant.fromEpochMilliseconds(1000L)
}
