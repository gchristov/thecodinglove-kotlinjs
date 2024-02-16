package com.gchristov.thecodinglove.selfdestruct.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface SelfDestructUseCase {
    suspend operator fun invoke() : Either<Throwable, Unit>
}

internal class RealSelfDestructUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val selfDestructSlackRepository: SelfDestructSlackRepository,
) : SelfDestructUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(): Either<Throwable, Unit> = withContext(dispatcher) {
        val slackSelfDestruct = async {
            log.debug(tag, "Slack self-destruct")
            selfDestructSlackRepository.selfDestruct()
        }
        either {
            slackSelfDestruct.await().bind()
        }
    }
}