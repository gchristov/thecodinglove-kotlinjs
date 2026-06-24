package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackSearchResultCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class RealSlackShuffleSearchUseCaseTest {
    @Test
    fun shuffleErrorPropagates(): TestResult = runBlockingTest(
        shuffleResult = Either.Left(Throwable("Shuffle failed")),
    ) { useCase, repository, _ ->
        val actual = useCase.invoke(SlackShuffleSearchUseCase.Dto(
            responseUrl = TestResponseUrl,
            searchSessionId = TestSessionId,
        ))
        assertTrue { actual.isLeft() }
        repository.assertPostMessageToUrlCalledTimes(0)
    }

    @Test
    fun shuffleWithNoSessionIsSwallowed(): TestResult = runBlockingTest(
        shuffleResult = Either.Right(SlackSearchResultCreator.noSession()),
    ) { useCase, repository, _ ->
        val actual = useCase.invoke(SlackShuffleSearchUseCase.Dto(
            responseUrl = TestResponseUrl,
            searchSessionId = TestSessionId,
        ))
        assertTrue { actual.isRight() }
        repository.assertPostMessageToUrlCalledTimes(0)
    }

    @Test
    fun shuffleSuccessPostsMessage(): TestResult = runBlockingTest(
        shuffleResult = Either.Right(SlackSearchResultCreator.success()),
    ) { useCase, repository, _ ->
        val actual = useCase.invoke(SlackShuffleSearchUseCase.Dto(
            responseUrl = TestResponseUrl,
            searchSessionId = TestSessionId,
        ))
        assertTrue { actual.isRight() }
        repository.assertPostMessageToUrlCalledTimes(1)
    }

    private fun runBlockingTest(
        shuffleResult: Either<Throwable, SlackSearchRepository.SearchResultDto> = Either.Right(SlackSearchResultCreator.success()),
        postMessageToUrlResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackShuffleSearchUseCase, FakeSlackRepository, FakeSlackSearchRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository(postMessageToUrlResult = postMessageToUrlResult)
        val searchRepository = FakeSlackSearchRepository(shuffleResult = shuffleResult)
        testBlock(
            RealSlackShuffleSearchUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                slackSearchRepository = searchRepository,
                slackRepository = repository,
                slackMessageFactory = FakeSlackMessageFactory(),
            ),
            repository,
            searchRepository,
        )
    }
}

private const val TestResponseUrl = "https://response.url"
private const val TestSessionId = "session_123"
