package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.usecase.SlackEnsureAuthenticatedUseCase
import kotlin.test.assertEquals

class FakeSlackEnsureAuthenticatedUseCase(
    private val invocationResult: Either<Throwable, SlackEnsureAuthenticatedUseCase.Result> =
        Either.Right(SlackEnsureAuthenticatedUseCase.Result.Authenticated),
) : SlackEnsureAuthenticatedUseCase {
    private var invocations = 0

    override suspend fun invoke(dto: SlackEnsureAuthenticatedUseCase.Dto): Either<Throwable, SlackEnsureAuthenticatedUseCase.Result> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
    fun assertNotInvoked() = assertEquals(expected = 0, actual = invocations)
}
