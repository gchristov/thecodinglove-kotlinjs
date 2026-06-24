package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.ParameterMap
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.adapter.http.model.ApiUpdateSearchSessionState
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchRepository
import com.gchristov.thecodinglove.search.testfixtures.SearchSessionCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.DeserializationStrategy
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateSearchSessionStateHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Put, config.method)
        assertEquals("/api/search/session-state", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun nullBodyReturnsError(): TestResult = runBlockingTest(body = null) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Request body is invalid"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun sessionNotFoundReturnsError(): TestResult = runBlockingTest(
        searchSession = null,
    ) { handler, request, response ->
        handler.handleHttpRequest(request, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Search session not found: searchSessionId=$TestSessionId"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun sentStateUpdatesSession(): TestResult = runBlockingTest(
        body = ApiUpdateSearchSessionState(
            searchSessionId = TestSessionId,
            state = ApiUpdateSearchSessionState.ApiState.Sent,
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
    fun selfDestructStateUpdatesSession(): TestResult = runBlockingTest(
        body = ApiUpdateSearchSessionState(
            searchSessionId = TestSessionId,
            state = ApiUpdateSearchSessionState.ApiState.SelfDestruct,
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
        body: ApiUpdateSearchSessionState? = ApiUpdateSearchSessionState(
            searchSessionId = TestSessionId,
            state = ApiUpdateSearchSessionState.ApiState.Sent,
        ),
        searchSession: SearchSession? = SearchSessionCreator.searchSession(
            id = TestSessionId,
            query = "kotlin",
        ),
        testBlock: suspend (UpdateSearchSessionStateHttpHandler, FakeUpdateSessionRequest, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val request = FakeUpdateSessionRequest(fakeBody = body)
        val response = FakeHttpResponse()
        val handler = UpdateSearchSessionStateHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            searchRepository = FakeSearchRepository(searchSession = searchSession),
        )
        testBlock(handler, request, response)
    }
}

private const val TestSessionId = "session_123"

private class FakeUpdateSessionRequest(
    private val fakeBody: ApiUpdateSearchSessionState?,
) : HttpRequest {
    override val baseURL = ""
    override val hostname = ""
    override val ip = ""
    override val ips: Array<String>? = null
    override val method = ""
    override val path = ""
    override val protocol = ""
    override val headers: ParameterMap = object : ParameterMap {
        override fun <T> get(key: String): T? = null
    }
    override val query: ParameterMap = object : ParameterMap {
        override fun <T> get(key: String): T? = null
    }
    override val bodyString: String? = null
    override val body: Any? get() = fakeBody

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>,
    ): Either<Throwable, T?> = Either.Right(fakeBody as T?)
}
