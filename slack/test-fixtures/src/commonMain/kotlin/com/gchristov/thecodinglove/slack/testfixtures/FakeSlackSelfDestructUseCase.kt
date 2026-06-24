package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSelfDestructUseCase
import kotlin.test.assertEquals

class FakeSlackSelfDestructUseCase(
    private val invocationResult: Either<Throwable, Unit> = Either.Right(Unit),
) : SlackSelfDestructUseCase {
    private var invocations = 0

    override suspend fun invoke(): Either<Throwable, Unit> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
