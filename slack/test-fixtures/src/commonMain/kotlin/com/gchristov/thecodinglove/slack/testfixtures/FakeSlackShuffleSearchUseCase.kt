package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.usecase.SlackShuffleSearchUseCase
import kotlin.test.assertEquals

class FakeSlackShuffleSearchUseCase(
    private val invocationResult: Either<Throwable, Unit> = Either.Right(Unit),
) : SlackShuffleSearchUseCase {
    private var invocations = 0

    override suspend fun invoke(dto: SlackShuffleSearchUseCase.Dto): Either<Throwable, Unit> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
    fun assertNotInvoked() = assertEquals(expected = 0, actual = invocations)
}
