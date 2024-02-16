package com.gchristov.thecodinglove.statistics.domain.port

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

interface StatisticsSlackRepository {
    suspend fun slackStatistics(): Either<Throwable, StatisticsReport.SlackStatistics>
}