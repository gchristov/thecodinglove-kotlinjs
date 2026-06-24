package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackAuthHttpRequest
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackAuthUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackAuthHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/slack/auth", config.path)
        assertEquals(ContentType.Application.FormUrlEncoded, config.contentType)
    }

    @Test
    fun authCancelledRedirectsToHome(): TestResult = runBlockingTest(
        authResult = Either.Left(SlackAuthUseCase.Error.Cancelled()),
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertRedirect("/")
    }

    @Test
    fun authErrorRedirectsToErrorPage(): TestResult = runBlockingTest(
        authResult = Either.Left(SlackAuthUseCase.Error.Other()),
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertRedirect("/slack/auth/error")
    }

    @Test
    fun authSuccessWithNoStateRedirectsToSuccess(): TestResult = runBlockingTest(
        authResult = Either.Right(Unit),
        fakeCode = "auth_code",
        fakeState = null,
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertRedirect("/slack/auth/success")
    }

    private fun runBlockingTest(
        authResult: Either<SlackAuthUseCase.Error, Unit> = Either.Right(Unit),
        fakeCode: String? = null,
        fakeState: String? = null,
        testBlock: suspend (SlackAuthHttpHandler, FakeSlackAuthHttpRequest, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val authUseCase = FakeSlackAuthUseCase(invocationResult = authResult)
        val sendUseCase = FakeSlackSendSearchUseCase()
        val request = FakeSlackAuthHttpRequest(fakeCode = fakeCode, fakeState = fakeState)
        val response = FakeHttpResponse()
        val handler = SlackAuthHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            slackAuthUseCase = authUseCase,
            slackSendSearchUseCase = sendUseCase,
            analytics = FakeAnalytics(),
        )
        testBlock(handler, request, response)
    }
}
