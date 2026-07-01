package com.gchristov.thecodinglove.selfdestruct.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RealSelfDestructUseCaseTest {
    @Test
    fun selfDestructIsANoOp(): TestResult = runTest {
        val useCase = RealSelfDestructUseCase(
            dispatcher = FakeCoroutineDispatcher,
            log = FakeLogger,
        )
        assertEquals(expected = Either.Right(Unit), actual = useCase.invoke())
    }
}
