package com.gchristov.thecodinglove.searchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.search.usecase.PreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchdata.model.SearchError
import kotlin.test.assertEquals

class FakePreloadSearchResultUseCase(
    var invocationResult: Either<SearchError, Unit>
) : PreloadSearchResultUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally
    private var invocations = 0
    private var lastSearchSessionId: String? = null

    override suspend fun invoke(searchSessionId: String): Either<SearchError, Unit> {
        lastSearchSessionId = searchSessionId
        invocations++
        return searchResponse.execute(invocationResult)
    }

    fun assertInvokedOnce() {
        assertEquals(
            expected = 1,
            actual = invocations
        )
    }

    fun assertSearchSessionId(searchSessionId: String) {
        assertEquals(
            expected = searchSessionId,
            actual = lastSearchSessionId
        )
    }
}