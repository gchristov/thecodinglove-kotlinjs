package com.gchristov.thecodinglove.statistics.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.search.proto.http.SearchApiRepository
import com.gchristov.thecodinglove.statistics.adapter.search.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository

internal class RealStatisticsSearchRepository(
    private val searchApiRepository: SearchApiRepository,
) : StatisticsSearchRepository {
    override suspend fun searchStatistics(): Either<Throwable, StatisticsReport.SearchStatistics> =
        searchApiRepository.searchStatistics().map { it.toStatistics() }
}