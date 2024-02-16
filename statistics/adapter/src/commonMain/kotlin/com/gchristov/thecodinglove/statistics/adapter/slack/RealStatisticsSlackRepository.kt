package com.gchristov.thecodinglove.statistics.adapter.slack

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.adapter.slack.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.adapter.slack.model.ApiSlackStatistics
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository
import io.ktor.client.call.*

internal class RealStatisticsSlackRepository(
    private val slackStatisticsApi: SlackStatisticsApi,
) : StatisticsSlackRepository {
    override suspend fun slackStatistics(): Either<Throwable, StatisticsReport.SlackStatistics> = try {
        val response: ApiSlackStatistics = slackStatisticsApi.statistics().body()
        Either.Right(response.toStatistics())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during Slack statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}