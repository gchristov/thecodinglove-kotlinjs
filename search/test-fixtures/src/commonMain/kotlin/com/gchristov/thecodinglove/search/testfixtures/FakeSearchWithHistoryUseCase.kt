package com.gchristov.thecodinglove.search.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeResponse
import com.gchristov.thecodinglove.common.test.execute
import com.gchristov.thecodinglove.search.domain.usecase.SearchWithHistoryUseCase
import kotlin.test.assertEquals

class FakeSearchWithHistoryUseCase(
    // Each result is passed as a response to the relevant invocation attempt
    var invocationResults: List<Either<SearchWithHistoryUseCase.Error, SearchWithHistoryUseCase.Result>>
) : SearchWithHistoryUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally

    private var invocations = 0

    override suspend fun invoke(
        dto: SearchWithHistoryUseCase.Dto
    ): Either<SearchWithHistoryUseCase.Error, SearchWithHistoryUseCase.Result> =
        searchResponse.execute(invocationResults[invocations++])

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

    fun assertInvokedTwice() {
        assertEquals(
            expected = 2,
            actual = invocations
        )
    }
}