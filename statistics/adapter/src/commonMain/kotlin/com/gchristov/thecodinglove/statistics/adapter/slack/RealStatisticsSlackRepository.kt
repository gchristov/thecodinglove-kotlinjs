package com.gchristov.thecodinglove.statistics.adapter.slack

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.adapter.slack.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.adapter.slack.model.ApiStatisticsSlack
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository
import io.ktor.client.call.*

internal class RealStatisticsSlackRepository(
    private val statisticsSlackServiceApi: StatisticsSlackServiceApi,
) : StatisticsSlackRepository {
    override suspend fun slackStatistics() = try {
        val response: ApiStatisticsSlack = statisticsSlackServiceApi.statistics().body()
        Either.Right(response.toStatistics())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}