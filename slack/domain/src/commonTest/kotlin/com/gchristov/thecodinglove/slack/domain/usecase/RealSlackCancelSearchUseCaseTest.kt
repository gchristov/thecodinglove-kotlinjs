package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSearchRepository
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RealSlackCancelSearchUseCaseTest {
    @Test
    fun cancelFailsWhenPostMessageFails(): TestResult = runBlockingTest(
        postMessageToUrlResult = Either.Left(Throwable("Post failed")),
    ) { useCase, _, _ ->
        val actual = useCase.invoke(SlackCancelSearchUseCase.Dto(
            responseUrl = TestResponseUrl,
            searchSessionId = TestSessionId,
        ))
        assertTrue { actual.isLeft() }
    }

    @Test
    fun cancelFailsWhenDeleteSessionFails(): TestResult = runBlockingTest(
        deleteSearchSessionResult = Either.Left(Throwable("Delete failed")),
    ) { useCase, _, _ ->
        val actual = useCase.invoke(SlackCancelSearchUseCase.Dto(
            responseUrl = TestResponseUrl,
            searchSessionId = TestSessionId,
        ))
        assertTrue { actual.isLeft() }
    }

    @Test
    fun cancelSuccessPostsMessageAndDeletesSession(): TestResult = runBlockingTest { useCase, repository, searchRepository ->
        val actual = useCase.invoke(SlackCancelSearchUseCase.Dto(
            responseUrl = TestResponseUrl,
            searchSessionId = TestSessionId,
        ))
        assertTrue { actual.isRight() }
        repository.assertPostMessageToUrlCalledTimes(1)
        searchRepository.assertDeleteSessionInvokedOnce()
    }

    private fun runBlockingTest(
        postMessageToUrlResult: Either<Throwable, Unit> = Either.Right(Unit),
        deleteSearchSessionResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackCancelSearchUseCase, FakeSlackRepository, FakeSlackSearchRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(postMessageToUrlResult = postMessageToUrlResult)
        val searchRepository = FakeSlackSearchRepository(deleteSearchSessionResult = deleteSearchSessionResult)
        testBlock(
            RealSlackCancelSearchUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackSearchRepository = searchRepository,
                slackMessageFactory = FakeSlackMessageFactory(),
                slackRepository = repository,
            ),
            repository,
            searchRepository,
        )
    }
}

private const val TestResponseUrl = "https://response.url"
private const val TestSessionId = "session_123"
