package com.gchristov.thecodinglove.selfdestruct.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.SelfDestructUseCase
import kotlin.test.assertEquals

class FakeSelfDestructUseCase(
    private val invocationResult: Either<Throwable, Unit> = Either.Right(Unit),
) : SelfDestructUseCase {
    private var invocations = 0

    override suspend fun invoke(): Either<Throwable, Unit> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
