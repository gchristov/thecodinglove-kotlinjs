package com.gchristov.thecodinglove.selfdestruct.adapter.slack

import arrow.core.Either
import com.gchristov.thecodinglove.selfdestruct.domain.port.SlackSelfDestructRepository

internal class RealSlackSelfDestructRepository(
    private val apiService: SlackSelfDestructApi,
) : SlackSelfDestructRepository {
    override suspend fun selfDestruct(): Either<Throwable, Unit> = try {
        apiService.selfDestruct()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during self-destruct${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}