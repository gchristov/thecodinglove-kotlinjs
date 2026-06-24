package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import kotlin.test.assertEquals

class FakeSlackAuthUseCase(
    private val invocationResult: Either<SlackAuthUseCase.Error, Unit> = Either.Right(Unit),
) : SlackAuthUseCase {
    private var invocations = 0

    override suspend fun invoke(dto: SlackAuthUseCase.Dto): Either<SlackAuthUseCase.Error, Unit> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
