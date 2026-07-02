package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackSearchSessionPostCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSelfDestructMenuInteractivityPubSubHandlerTest {
    @Test
    fun handleSelfDestructMenuPostsDelayMenuMessage(): TestResult = runBlockingTest { handler, searchRepository, repository ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_MENU).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        searchRepository.assertGetSessionPostInvokedOnce()
        repository.assertPostMessageToUrlCalledTimes(1)
    }

    @Test
    fun handleOtherActionSkips(): TestResult = runBlockingTest { handler, searchRepository, repository ->
        val payload = interactivityMessage(action = SlackActionName.SEND).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        searchRepository.assertGetSessionPostNotInvoked()
        repository.assertPostMessageToUrlCalledTimes(0)
    }

    @Test
    fun handleGetSessionPostErrorReturnsLeft(): TestResult = runBlockingTest(
        getSearchSessionPostResult = Either.Left(Throwable("Session not found")),
    ) { handler, _, repository ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_MENU).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(0)
    }

    @Test
    fun handlePostMessageErrorReturnsLeft(): TestResult = runBlockingTest(
        postMessageToUrlResult = Either.Left(Throwable("Post failed")),
    ) { handler, _, _ ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_MENU).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
    }

    private fun runBlockingTest(
        getSearchSessionPostResult: Either<Throwable, SlackSearchRepository.SearchSessionPostDto> = Either.Right(SlackSearchSessionPostCreator.post()),
        postMessageToUrlResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (
            SlackSelfDestructMenuInteractivityPubSubHandler,
            FakeSlackSearchRepository,
            FakeSlackRepository,
        ) -> Unit,
    ): TestResult = runTest {
        val searchRepository = FakeSlackSearchRepository(getSearchSessionPostResult = getSearchSessionPostResult)
        val repository = FakeSlackRepository(postMessageToUrlResult = postMessageToUrlResult)
        val handler = SlackSelfDestructMenuInteractivityPubSubHandler(
            slackSearchRepository = searchRepository,
            slackRepository = repository,
            slackMessageFactory = FakeSlackMessageFactory(),
            analytics = FakeAnalytics(),
        )
        testBlock(handler, searchRepository, repository)
    }
}
