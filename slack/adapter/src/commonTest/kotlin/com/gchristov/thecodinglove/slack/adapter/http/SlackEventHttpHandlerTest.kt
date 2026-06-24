package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackEvent
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackHttpRequest
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRevokeTokensUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackVerifyRequestUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackEventHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/slack/event", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun urlVerificationSendsChallenge(): TestResult = runBlockingTest(
        body = ApiSlackEvent.ApiUrlVerification(challenge = TestChallenge),
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Text.Plain.toString(),
            data = TestChallenge,
            status = 200,
            filePath = null,
        )
    }

    @Test
    fun tokensRevokedCallsRevokeUseCase(): TestResult = runBlockingTest(
        body = ApiSlackEvent.ApiCallback(
            teamId = "team_id",
            event = ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked(
                tokens = ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked.ApiTokens(
                    oAuth = listOf("token_1"),
                    bot = null,
                )
            ),
        ),
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    @Test
    fun tokensRevokedFailureSendsError(): TestResult = runBlockingTest(
        body = ApiSlackEvent.ApiCallback(
            teamId = "team_id",
            event = ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked(
                tokens = ApiSlackEvent.ApiCallback.ApiEvent.ApiTokensRevoked.ApiTokens(
                    oAuth = listOf("token_1"),
                    bot = null,
                )
            ),
        ),
        revokeResult = Either.Left(Throwable("Revoke failed")),
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Revoke failed"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun appUninstalledSendsEmptyResponse(): TestResult = runBlockingTest(
        body = ApiSlackEvent.ApiCallback(
            teamId = "team_id",
            event = ApiSlackEvent.ApiCallback.ApiEvent.ApiAppUninstalled,
        ),
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        body: ApiSlackEvent? = null,
        revokeResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackEventHttpHandler, FakeSlackHttpRequest, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val request = FakeSlackHttpRequest(fakeBody = body)
        val response = FakeHttpResponse()
        val handler = SlackEventHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            slackVerifyRequestUseCase = FakeSlackVerifyRequestUseCase(),
            slackConfig = TestSlackConfig,
            slackRevokeTokensUseCase = FakeSlackRevokeTokensUseCase(invocationResult = revokeResult),
            analytics = FakeAnalytics(),
        )
        testBlock(handler, request, response)
    }
}

private const val TestChallenge = "challenge_token"
private val TestSlackConfig = SlackConfigCreator.slackConfig()
