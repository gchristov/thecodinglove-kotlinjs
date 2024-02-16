package com.gchristov.thecodinglove.statistics.domain.port

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport

interface StatisticsSearchRepository {
    suspend fun searchStatistics(): Either<Throwable, StatisticsReport.SearchStatistics>
}