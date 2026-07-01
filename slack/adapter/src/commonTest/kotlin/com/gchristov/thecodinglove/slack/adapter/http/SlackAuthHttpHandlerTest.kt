package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubPublisher
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackAuthHttpRequest
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackAuthUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSelfDestructMessageCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackAuthHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/slack/auth", config.path)
        assertEquals(ContentType.Application.FormUrlEncoded, config.contentType)
    }

    @Test
    fun authCancelledRedirectsToHome(): TestResult = runBlockingTest(
        authResult = Either.Left(SlackAuthUseCase.Error.Cancelled()),
    ) { handler, request, response, _, _ ->
        handler.handleHttpRequest(request, response)
        response.assertRedirect("/")
    }

    @Test
    fun authErrorRedirectsToErrorPage(): TestResult = runBlockingTest(
        authResult = Either.Left(SlackAuthUseCase.Error.Other()),
    ) { handler, request, response, _, _ ->
        handler.handleHttpRequest(request, response)
        response.assertRedirect("/slack/auth/error")
    }

    @Test
    fun authSuccessWithNoStateRedirectsToSuccess(): TestResult = runBlockingTest(
        authResult = Either.Right(Unit),
        fakeCode = "auth_code",
        fakeState = null,
    ) { handler, request, response, _, _ ->
        handler.handleHttpRequest(request, response)
        response.assertRedirect("/slack/auth/success")
    }

    @Test
    fun authSuccessWithValidStateCallsSendSearch(): TestResult = runBlockingTest(
        authResult = Either.Right(Unit),
        fakeCode = "auth_code",
        fakeState = TestEncodedState,
    ) { handler, request, response, sendUseCase, _ ->
        handler.handleHttpRequest(request, response)
        sendUseCase.assertInvokedOnce()
        response.assertRedirect("/slack/auth/success")
    }

    @Test
    fun authSuccessWithSendSearchErrorSendsError(): TestResult = runBlockingTest(
        authResult = Either.Right(Unit),
        sendSearchResult = Either.Left(Throwable("Search failed")),
        fakeCode = "auth_code",
        fakeState = TestEncodedState,
    ) { handler, request, response, _, _ ->
        handler.handleHttpRequest(request, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Search failed"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun authSuccessWithSelfDestructMessageSchedulesSelfDestruct(): TestResult = runBlockingTest(
        authResult = Either.Right(Unit),
        sendSearchResult = Either.Right(SlackSelfDestructMessageCreator.pastMessage()),
        fakeCode = "auth_code",
        fakeState = TestEncodedState,
    ) { handler, request, response, _, pubSubPublisher ->
        handler.handleHttpRequest(request, response)
        pubSubPublisher.assertTopic(TestSelfDestructTopic)
        response.assertRedirect("/slack/auth/success")
    }

    @Test
    fun authSuccessWithNoSelfDestructMessageDoesNotSchedule(): TestResult = runBlockingTest(
        authResult = Either.Right(Unit),
        sendSearchResult = Either.Right(null),
        fakeCode = "auth_code",
        fakeState = TestEncodedState,
    ) { handler, request, response, _, pubSubPublisher ->
        handler.handleHttpRequest(request, response)
        pubSubPublisher.assertNotInvoked()
        response.assertRedirect("/slack/auth/success")
    }

    private fun runBlockingTest(
        authResult: Either<SlackAuthUseCase.Error, Unit> = Either.Right(Unit),
        sendSearchResult: Either<Throwable, SlackSelfDestructMessage?> = Either.Right(null),
        fakeCode: String? = null,
        fakeState: String? = null,
        testBlock: suspend (SlackAuthHttpHandler, FakeSlackAuthHttpRequest, FakeHttpResponse, FakeSlackSendSearchUseCase, FakePubSubPublisher) -> Unit,
    ): TestResult = runTest {
        val authUseCase = FakeSlackAuthUseCase(invocationResult = authResult)
        val sendUseCase = FakeSlackSendSearchUseCase(invocationResult = sendSearchResult)
        val pubSubPublisher = FakePubSubPublisher()
        val request = FakeSlackAuthHttpRequest(fakeCode = fakeCode, fakeState = fakeState)
        val response = FakeHttpResponse()
        val handler = SlackAuthHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            slackAuthUseCase = authUseCase,
            slackSendSearchUseCase = sendUseCase,
            pubSubPublisher = pubSubPublisher,
            slackConfig = SlackConfigCreator.slackConfig(selfDestructMessagePubSubTopic = TestSelfDestructTopic),
            analytics = FakeAnalytics(),
        )
        testBlock(handler, request, response, sendUseCase, pubSubPublisher)
    }
}

private const val TestSelfDestructTopic = "self_destruct_topic"

// Base64 of: {"search_session_id":"session_1","channel_id":"ch_1","team_id":"team_1","user_id":"user_1","response_url":"https://response.com","self_destruct_minutes":5}
private const val TestEncodedState =
    "eyJzZWFyY2hfc2Vzc2lvbl9pZCI6InNlc3Npb25fMSIsImNoYW5uZWxfaWQiOiJjaF8xIiwidGVhbV9pZCI6InRlYW1fMSIsInVzZXJfaWQiOiJ1c2VyXzEiLCJyZXNwb25zZV91cmwiOiJodHRwczovL3Jlc3BvbnNlLmNvbSIsInNlbGZfZGVzdHJ1Y3RfbWludXRlcyI6NX0="
