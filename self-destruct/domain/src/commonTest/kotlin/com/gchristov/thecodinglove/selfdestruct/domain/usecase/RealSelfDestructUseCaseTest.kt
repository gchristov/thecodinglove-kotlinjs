package com.gchristov.thecodinglove.selfdestruct.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RealSelfDestructUseCaseTest {
    @Test
    fun selfDestructFailsWhenRepositoryFails(): TestResult = runBlockingTest(
        selfDestructResult = Either.Left(Throwable("Slack API error")),
    ) { useCase ->
        assertTrue { useCase.invoke().isLeft() }
    }

    @Test
    fun selfDestructSucceeds(): TestResult = runBlockingTest(
        selfDestructResult = Either.Right(Unit),
    ) { useCase ->
        assertEquals(expected = Either.Right(Unit), actual = useCase.invoke())
    }

    private fun runBlockingTest(
        selfDestructResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SelfDestructUseCase) -> Unit,
    ): TestResult = runTest {
        testBlock(
            RealSelfDestructUseCase(
                dispatcher = FakeCoroutineDispatcher,
                log = FakeLogger,
                selfDestructSlackRepository = FakeSelfDestructSlackRepository(selfDestructResult),
            )
        )
    }
}

private class FakeSelfDestructSlackRepository(
    private val result: Either<Throwable, Unit>,
) : SelfDestructSlackRepository {
    override suspend fun selfDestruct() = result
}
