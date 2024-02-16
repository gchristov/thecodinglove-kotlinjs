package com.gchristov.thecodinglove.statistics.adapter.slack

import com.gchristov.thecodinglove.slack.proto.http.SlackServiceRepository
import com.gchristov.thecodinglove.statistics.adapter.slack.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository

internal class RealStatisticsSlackRepository(
    private val slackServiceRepository: SlackServiceRepository,
) : StatisticsSlackRepository {
    override suspend fun slackStatistics() = slackServiceRepository.slackStatistics().map { it.toStatistics() }
}