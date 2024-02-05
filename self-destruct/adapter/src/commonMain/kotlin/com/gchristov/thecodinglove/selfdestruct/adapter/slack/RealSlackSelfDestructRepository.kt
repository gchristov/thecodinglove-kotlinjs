package com.gchristov.thecodinglove.selfdestruct.adapter.slack

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.selfdestruct.domain.port.SlackSelfDestructRepository
import kotlinx.coroutines.CoroutineDispatcher

class RealSlackSelfDestructRepository(
    private val dispatcher: CoroutineDispatcher,
    private val client: NetworkClient.Json,
) : SlackSelfDestructRepository {
    override suspend fun selfDestruct(): Either<Throwable, Unit> {
        TODO("Not yet implemented")
    }
}