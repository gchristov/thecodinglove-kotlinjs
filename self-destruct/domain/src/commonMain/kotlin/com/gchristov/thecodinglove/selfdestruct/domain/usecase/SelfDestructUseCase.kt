package com.gchristov.thecodinglove.selfdestruct.domain.usecase

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SelfDestructUseCase {
    suspend operator fun invoke() : Either<Throwable, Unit>
}

// Self-destruct is now scheduled and handled entirely by the slack service via Cloud Tasks + PubSub
// (see SlackSelfDestructMessagePubSubHandler). This service no longer calls Slack - it stays deployed
// as a no-op so its still-live Cloud Scheduler job keeps succeeding until it's decommissioned.
internal class RealSelfDestructUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
) : SelfDestructUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(): Either<Throwable, Unit> = withContext(dispatcher) {
        log.debug(tag, "Self-destruct is a no-op; handled by the slack service")
        Either.Right(Unit)
    }
}