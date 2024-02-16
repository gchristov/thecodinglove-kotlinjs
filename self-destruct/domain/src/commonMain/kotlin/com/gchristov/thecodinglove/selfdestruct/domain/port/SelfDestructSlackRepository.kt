package com.gchristov.thecodinglove.selfdestruct.domain.port

import arrow.core.Either

interface SelfDestructSlackRepository {
    suspend fun selfDestruct(): Either<Throwable, Unit>
}