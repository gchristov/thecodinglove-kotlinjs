package com.gchristov.thecodinglove.search.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeResponse
import com.gchristov.thecodinglove.common.test.execute
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import kotlin.test.assertEquals

class FakeSearchUseCase(
    var invocationResult: Either<SearchUseCase.Error, SearchUseCase.Result>
) : SearchUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally
    private var invocations = 0
    private var lastType: SearchUseCase.Type? = null

    override suspend fun invoke(
        dto: SearchUseCase.Dto
    ): Either<SearchUseCase.Error, SearchUseCase.Result> {
        lastType = dto.type
        invocations++
        return searchResponse.execute(invocationResult)
    }

    fun assertInvokedOnce() {
        assertEquals(
            expected = 1,
            actual = invocations
        )
    }

    fun assertSearchType(type: SearchUseCase.Type) {
        assertEquals(
            expected = type,
            actual = lastType
        )
    }
}