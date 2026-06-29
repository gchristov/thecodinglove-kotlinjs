package com.gchristov.thecodinglove.selfdestruct.adapter.slack

import com.gchristov.thecodinglove.common.network.safeApiCall
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository

internal class RealSelfDestructSlackRepository(
    private val selfDestructSlackServiceApi: SelfDestructSlackServiceApi,
) : SelfDestructSlackRepository {
    override suspend fun selfDestruct() = safeApiCall("Error during self-destruct") {
        selfDestructSlackServiceApi.selfDestruct()
        Unit
    }
}
