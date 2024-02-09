package com.gchristov.thecodinglove.statistics.domain.port

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

interface SearchStatisticsRepository {
    suspend fun statistics(): Either<Throwable, StatisticsReport.SearchStatistics>
}