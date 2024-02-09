package com.gchristov.thecodinglove.selfdestruct.domain.port

import arrow.core.Either

interface SlackSelfDestructRepository {
    suspend fun selfDestruct(): Either<Throwable, Unit>
}