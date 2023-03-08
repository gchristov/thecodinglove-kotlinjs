package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicetestfixtures.FakeApiResponse
import com.gchristov.thecodinglove.commonservicetestfixtures.FakeApiServiceRegister
import com.gchristov.thecodinglove.commonservicetestfixtures.FakePubSubSender
import com.gchristov.thecodinglove.kmpcommontest.FakeLogger
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubTopic
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.searchtestfixtures.FakeSearchApiRequest
import com.gchristov.thecodinglove.searchtestfixtures.FakeSearchUseCase
import com.gchristov.thecodinglove.searchtestfixtures.SearchResultCreator
import io.ktor.client.engine.js.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.di.bindings.ErasedContext.value
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SearchApiServiceTest {
    @Test
    fun register(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchError.Empty)
    ) { service, _, _, _, _, register ->
        service.register()
        register.assertInvokedOnce()
    }

    @Test
    fun handleRequestSuccess(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = null,
            searchInvocationResult = Either.Right(expectedResult)
        ) { service, pubSubSender, searchUseCase, request, response, register ->
            val actualResult = service.handleRequest(
                request = request,
                response = response
            )
            register.assertNotInvoked()
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchUseCase.Type.NewSession(query = TestSearchQuery)
            )
            pubSubSender.assertEquals(
                topic = PreloadSearchPubSubTopic,
                body = Json.encodeToString(PreloadSearchPubSubMessage(TestSearchSessionId))
            )
            response.assertEquals(
                header = "Content-Type",
                headerValue = "application/json",
                data = """
                        {"search_session_id":"session_123","query":"test","post":{"title":"post","url":"url","image_url":"imageUrl"},"total_posts":1}
                       """.trimIndent(),
                status = 200
            )
            assertTrue { actualResult.isRight() }
        }
    }

    @Test
    fun handleRequestSuccessWithSessionId(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = TestSearchSessionId,
            searchQuery = TestSearchQuery,
            searchInvocationResult = Either.Right(expectedResult)
        ) { service, pubSubSender, searchUseCase, request, response, register ->
            val actualResult = service.handleRequest(
                request = request,
                response = response
            )
            register.assertNotInvoked()
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchUseCase.Type.WithSessionId(TestSearchSessionId)
            )
            pubSubSender.assertEquals(
                topic = PreloadSearchPubSubTopic,
                body = Json.encodeToString(PreloadSearchPubSubMessage(TestSearchSessionId))
            )
            response.assertEquals(
                header = "Content-Type",
                headerValue = "application/json",
                data = """
                        {"search_session_id":"session_123","query":"test","post":{"title":"post","url":"url","image_url":"imageUrl"},"total_posts":1}
                       """.trimIndent(),
                status = 200
            )
            assertTrue { actualResult.isRight() }
        }
    }

    @Test
    fun handleRequestError(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchError.Empty)
    ) { service, pubSubSender, searchUseCase, request, response, register ->
        val actualResult = service.handleRequest(
            request = request,
            response = response
        )
        register.assertNotInvoked()
        searchUseCase.assertInvokedOnce()
        pubSubSender.assertNotInvoked()
        assertEquals(
            expected = Either.Left(SearchError.Empty),
            actual = actualResult
        )
    }

    private fun runBlockingTest(
        searchSessionId: String? = TestSearchSessionId,
        searchQuery: String? = TestSearchQuery,
        searchInvocationResult: Either<SearchError, SearchUseCase.Result>,
        testBlock: suspend (SearchApiService, FakePubSubSender, FakeSearchUseCase, FakeSearchApiRequest, FakeApiResponse, FakeApiServiceRegister) -> Unit
    ): TestResult = runTest {
        val pubSubSender = FakePubSubSender()
        val searchUseCase = FakeSearchUseCase(
            invocationResult = searchInvocationResult
        )
        val request = FakeSearchApiRequest(
            fakeSearchSessionId = searchSessionId,
            fakeSearchQuery = searchQuery
        )
        val response = FakeApiResponse()
        val register = FakeApiServiceRegister()
        val service = SearchApiService(
            apiServiceRegister = register,
            jsonSerializer = Json,
            log = FakeLogger,
            searchUseCase = searchUseCase
        )
        testBlock(service, pubSubSender, searchUseCase, request, response, register)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"