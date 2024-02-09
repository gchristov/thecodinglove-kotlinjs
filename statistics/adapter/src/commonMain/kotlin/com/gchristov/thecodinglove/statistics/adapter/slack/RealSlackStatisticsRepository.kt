package com.gchristov.thecodinglove.statistics.adapter.slack

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.adapter.slack.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.adapter.slack.model.ApiSlackStatistics
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.SlackStatisticsRepository
import io.ktor.client.call.*

internal class RealSlackStatisticsRepository(
    private val apiService: SlackStatisticsApi,
) : SlackStatisticsRepository {
    override suspend fun statistics(): Either<Throwable, StatisticsReport.SlackStatistics> = try {
        val response: ApiSlackStatistics = apiService.statistics().body()
        Either.Right(response.toStatistics())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during Slack statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}