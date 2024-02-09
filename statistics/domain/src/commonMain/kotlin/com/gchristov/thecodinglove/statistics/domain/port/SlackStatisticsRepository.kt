package com.gchristov.thecodinglove.statistics.domain.port

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

interface SlackStatisticsRepository {
    suspend fun statistics(): Either<Throwable, StatisticsReport.SlackStatistics>
}