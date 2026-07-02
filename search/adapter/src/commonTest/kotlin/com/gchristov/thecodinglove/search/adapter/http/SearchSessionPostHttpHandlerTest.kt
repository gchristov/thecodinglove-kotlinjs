package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchHttpRequest
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchRepository
import com.gchristov.thecodinglove.search.testfixtures.SearchPostCreator
import com.gchristov.thecodinglove.search.testfixtures.SearchSessionCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchSessionPostHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/search/session-post", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun sessionNotFoundSendsError(): TestResult = runBlockingTest(
        searchSession = null,
    ) { handler, response ->
        handler.handleHttpRequest(FakeSearchHttpRequest(fakeSearchSessionId = TestSessionId), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Search session not found: searchSessionId=$TestSessionId"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun sessionFoundReturnsPost(): TestResult = runBlockingTest(
        searchSession = SearchSessionCreator.searchSession(
            id = TestSessionId,
            query = "kotlin",
        ).copy(currentPost = SearchPostCreator.defaultPost(), totalPosts = 5),
    ) { handler, response ->
        handler.handleHttpRequest(FakeSearchHttpRequest(fakeSearchSessionId = TestSessionId), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"search_query":"kotlin","attachment_title":"post","attachment_url":"url","attachment_image_url":"imageUrl","total_posts":5}""",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        searchSession: SearchSession? = SearchSessionCreator.searchSession(
            id = TestSessionId,
            query = "kotlin",
        ).copy(currentPost = SearchPostCreator.defaultPost(), totalPosts = 5),
        testBlock: suspend (SearchSessionPostHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = SearchSessionPostHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            searchRepository = FakeSearchRepository(searchSession = searchSession),
        )
        testBlock(handler, response)
    }
}

private const val TestSessionId = "session_123"
