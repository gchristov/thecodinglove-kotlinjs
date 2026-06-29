package com.gchristov.thecodinglove.statistics.adapter.slack

import com.gchristov.thecodinglove.common.network.safeApiCall
import com.gchristov.thecodinglove.statistics.adapter.slack.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.adapter.slack.model.ApiStatisticsSlack
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository
import io.ktor.client.call.*

internal class RealStatisticsSlackRepository(
    private val statisticsSlackServiceApi: StatisticsSlackServiceApi,
) : StatisticsSlackRepository {
    override suspend fun slackStatistics() = safeApiCall("Error during statistics") {
        statisticsSlackServiceApi.statistics().body<ApiStatisticsSlack>().toStatistics()
    }
}
