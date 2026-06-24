package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.usecase.SlackRevokeTokensUseCase
import kotlin.test.assertEquals

class FakeSlackRevokeTokensUseCase(
    private val invocationResult: Either<Throwable, Unit> = Either.Right(Unit),
) : SlackRevokeTokensUseCase {
    private var invocations = 0

    override suspend fun invoke(dto: SlackRevokeTokensUseCase.Dto): Either<Throwable, Unit> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
    fun assertNotInvoked() = assertEquals(expected = 0, actual = invocations)
}
