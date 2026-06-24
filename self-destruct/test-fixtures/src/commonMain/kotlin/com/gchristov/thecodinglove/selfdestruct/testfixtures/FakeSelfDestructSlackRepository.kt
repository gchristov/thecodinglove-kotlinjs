package com.gchristov.thecodinglove.selfdestruct.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import kotlin.test.assertEquals

class FakeSelfDestructSlackRepository(
    private val selfDestructResult: Either<Throwable, Unit> = Either.Right(Unit),
) : SelfDestructSlackRepository {
    private var invocations = 0

    override suspend fun selfDestruct(): Either<Throwable, Unit> {
        invocations++
        return selfDestructResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}
