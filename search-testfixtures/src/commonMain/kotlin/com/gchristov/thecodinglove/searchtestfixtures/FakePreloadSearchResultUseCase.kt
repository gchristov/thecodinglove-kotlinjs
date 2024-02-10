package com.gchristov.thecodinglove.searchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeResponse
import com.gchristov.thecodinglove.common.test.execute
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchdata.usecase.PreloadSearchResultUseCase
import kotlin.test.assertEquals

class FakePreloadSearchResultUseCase(
    var invocationResult: Either<SearchError, Unit>
) : PreloadSearchResultUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally
    private var invocations = 0
    private var lastSearchSessionId: String? = null

    override suspend fun invoke(dto: PreloadSearchResultUseCase.Dto): Either<SearchError, Unit> {
        lastSearchSessionId = dto.searchSessionId
        invocations++
        return searchResponse.execute(invocationResult)
    }

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = invocations
        )
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