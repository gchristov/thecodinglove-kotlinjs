package com.gchristov.thecodinglove.slackweb.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.ParameterMap
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slackweb.domain.model.SlackConfig
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.DeserializationStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackAuthRedirectHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/slack/auth", config.path)
        assertEquals(ContentType.Application.Any, config.contentType)
    }

    @Test
    fun redirectsToSlackOAuth(): TestResult = runBlockingTest { handler, response ->
        handler.handleHttpRequest(FakeEmptyHttpRequest, response)
        response.assertRedirect("https://slack.com/oauth/v2/authorize?client_id=${TestSlackConfig.clientId}&scope=commands")
    }

    private fun runBlockingTest(
        testBlock: suspend (SlackAuthRedirectHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = SlackAuthRedirectHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            slackConfig = TestSlackConfig,
            analytics = FakeAnalytics(),
        )
        testBlock(handler, response)
    }
}

private val TestSlackConfig = SlackConfig(clientId = "test_client_id")

private object FakeEmptyHttpRequest : HttpRequest {
    override val baseURL = ""
    override val hostname = ""
    override val ip = ""
    override val ips: Array<String>? = null
    override val method = ""
    override val path = ""
    override val protocol = ""
    override val headers: ParameterMap = object : ParameterMap { override fun <T> get(key: String): T? = null }
    override val query: ParameterMap = object : ParameterMap { override fun <T> get(key: String): T? = null }
    override val body: Any? = null
    override val bodyString: String? = null
    override fun <T> decodeBodyFromJson(jsonSerializer: JsonSerializer, strategy: DeserializationStrategy<T>): Either<Throwable, T?> = Either.Right(null)
}
