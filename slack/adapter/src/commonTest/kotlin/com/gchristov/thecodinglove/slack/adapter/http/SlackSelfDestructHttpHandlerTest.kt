package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackHttpRequest
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackSelfDestructHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/slack/self-destruct", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun selfDestructErrorSendsError(): TestResult = runBlockingTest(
        selfDestructResult = Either.Left(Throwable("Self-destruct failed")),
    ) { handler, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Self-destruct failed"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun selfDestructSuccessSendsEmpty(): TestResult = runBlockingTest { handler, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        selfDestructResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackSelfDestructHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = SlackSelfDestructHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            selfDestructUseCase = FakeSlackSelfDestructUseCase(invocationResult = selfDestructResult),
        )
        testBlock(handler, response)
    }
}
