package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicetestfixtures.FakeApiResponse
import com.gchristov.thecodinglove.commonservicetestfixtures.FakeApiServiceRegister
import com.gchristov.thecodinglove.commonservicetestfixtures.FakePubSubSender
import com.gchristov.thecodinglove.searchdata.api.toSearchResult
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.searchtestfixtures.FakeSearchApiRequest
import com.gchristov.thecodinglove.searchtestfixtures.FakeSearchWithSessionUseCase
import com.gchristov.thecodinglove.searchtestfixtures.SearchWithSessionResultCreator
import io.ktor.client.engine.js.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.kodein.di.bindings.ErasedContext.value
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchApiServiceTest {
    @Test
    fun register(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchWithSessionInvocationResult = Either.Left(SearchError.Empty)
    ) { service, _, _, _, _, register ->
        service.register()
        register.assertInvokedOnce()
    }

    @Test
    fun handleRequestSuccess(): TestResult {
        val expectedResult = SearchWithSessionResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        val expectedPreloadPubSubMessage =
            PreloadPubSubService.buildPubSubMessage(TestSearchSessionId)
        return runBlockingTest(
            searchSessionId = null,
            searchWithSessionInvocationResult = Either.Right(expectedResult)
        ) { service, pubSubSender, searchUseCase, request, response, register ->
            service.handleRequest(
                request = request,
                response = response
            )
            register.assertNotInvoked()
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchWithSessionUseCase.Type.NewSession(query = TestSearchQuery)
            )
            pubSubSender.assertEquals(
                topic = expectedPreloadPubSubMessage.topic,
                body = Json.encodeToString(expectedPreloadPubSubMessage)
            )
            response.assertEquals(
                header = "Content-Type",
                headerValue = "application/json",
                data = Json.encodeToString(expectedResult.toSearchResult()),
                status = 200
            )
        }
    }

    @Test
    fun handleRequestSuccessWithSessionId(): TestResult {
        val expectedResult = SearchWithSessionResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        val expectedPreloadPubSubMessage =
            PreloadPubSubService.buildPubSubMessage(TestSearchSessionId)
        return runBlockingTest(
            searchSessionId = TestSearchSessionId,
            searchQuery = TestSearchQuery,
            searchWithSessionInvocationResult = Either.Right(expectedResult)
        ) { service, pubSubSender, searchUseCase, request, response, register ->
            service.handleRequest(
                request = request,
                response = response
            )
            register.assertNotInvoked()
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchWithSessionUseCase.Type.WithSessionId(
                    query = TestSearchQuery,
                    sessionId = TestSearchSessionId
                )
            )
            pubSubSender.assertEquals(
                topic = expectedPreloadPubSubMessage.topic,
                body = Json.encodeToString(expectedPreloadPubSubMessage)
            )
            response.assertEquals(
                header = "Content-Type",
                headerValue = "application/json",
                data = Json.encodeToString(expectedResult.toSearchResult()),
                status = 200
            )
        }
    }

    @Test
    fun handleRequestError(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchWithSessionInvocationResult = Either.Left(SearchError.Empty)
    ) { service, pubSubSender, searchUseCase, request, response, register ->
        service.handleRequest(
            request = request,
            response = response
        )
        register.assertNotInvoked()
        searchUseCase.assertInvokedOnce()
        pubSubSender.assertNotInvoked()
        response.assertEquals(
            header = "Content-Type",
            headerValue = "application/json",
            data = "{\"errorMessage\":\"Something unexpected happened. Please try again.\"}",
            status = 400
        )
    }

    private fun runBlockingTest(
        searchSessionId: String? = TestSearchSessionId,
        searchQuery: String? = TestSearchQuery,
        searchWithSessionInvocationResult: Either<SearchError, SearchWithSessionUseCase.Result>,
        testBlock: suspend (SearchApiService, FakePubSubSender, FakeSearchWithSessionUseCase, FakeSearchApiRequest, FakeApiResponse, FakeApiServiceRegister) -> Unit
    ): TestResult = runTest {
        val pubSubSender = FakePubSubSender()
        val searchWithSessionUseCase = FakeSearchWithSessionUseCase(
            invocationResult = searchWithSessionInvocationResult
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
            pubSubSender = pubSubSender,
            searchWithSessionUseCase = searchWithSessionUseCase
        )
        testBlock(service, pubSubSender, searchWithSessionUseCase, request, response, register)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"