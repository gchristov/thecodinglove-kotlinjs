package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SelfDestructSlackMessageEvent
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackSelfDestructMessagePubSubHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/self-destruct/message", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleInvokesSelfDestructUseCaseWithEventFields(): TestResult = runBlockingTest { handler, useCase ->
        val result = handler.handle(TestEvent)
        assertTrue { result.isRight() }
        useCase.assertInvokedOnce()
        useCase.assertInvokedWith(TestEvent.id)
    }

    @Test
    fun handlePropagatesUseCaseError(): TestResult = runBlockingTest(
        selfDestructResult = Either.Left(Throwable("Self-destruct failed")),
    ) { handler, _ ->
        val result = handler.handle(TestEvent)
        assertTrue { result.isLeft() }
    }

    private fun runBlockingTest(
        selfDestructResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackSelfDestructMessagePubSubHandler, FakeSlackSelfDestructUseCase) -> Unit,
    ): TestResult = runTest {
        val useCase = FakeSlackSelfDestructUseCase(invocationResult = selfDestructResult)
        val handler = SlackSelfDestructMessagePubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            pubSubDecoder = FakePubSubDecoder(FakePubSubRequest(null, SelfDestructSlackMessageEvent.serializer())),
            selfDestructUseCase = useCase,
        )
        testBlock(handler, useCase)
    }
}

private val TestEvent = SelfDestructSlackMessageEvent(
    id = "message_id",
    userId = "user_id",
    channelId = "channel_id",
    messageTs = "message_ts",
)
