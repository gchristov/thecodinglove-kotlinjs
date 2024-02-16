package com.gchristov.thecodinglove.selfdestruct.adapter.slack

import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import com.gchristov.thecodinglove.slack.proto.http.SlackServiceRepository

internal class RealSelfDestructSlackRepository(
    private val slackServiceRepository: SlackServiceRepository,
) : SelfDestructSlackRepository {
    override suspend fun selfDestruct() = slackServiceRepository.selfDestruct()
}