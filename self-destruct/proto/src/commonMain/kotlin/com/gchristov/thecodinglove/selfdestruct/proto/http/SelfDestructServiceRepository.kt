package com.gchristov.thecodinglove.selfdestruct.proto.http

import arrow.core.Either

interface SelfDestructServiceRepository {
    suspend fun selfDestruct(): Either<Throwable, Unit>
}

internal class RealSelfDestructServiceRepository(
    private val selfDestructServiceApi: SelfDestructServiceApi,
) : SelfDestructServiceRepository {
    override suspend fun selfDestruct() = try {
        selfDestructServiceApi.selfDestruct()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during self-destruct${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}