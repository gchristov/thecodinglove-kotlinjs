package com.gchristov.thecodinglove.selfdestruct.adapter.slack

import arrow.core.Either
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository

internal class RealSelfDestructSlackRepository(
    private val selfDestructSlackServiceApi: SelfDestructSlackServiceApi,
) : SelfDestructSlackRepository {
    override suspend fun selfDestruct() = try {
        selfDestructSlackServiceApi.selfDestruct()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during self-destruct${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}