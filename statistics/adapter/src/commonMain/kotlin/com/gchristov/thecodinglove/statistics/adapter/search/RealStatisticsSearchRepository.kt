package com.gchristov.thecodinglove.statistics.adapter.search

import com.gchristov.thecodinglove.search.proto.http.SearchServiceRepository
import com.gchristov.thecodinglove.statistics.adapter.search.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository

internal class RealStatisticsSearchRepository(
    private val searchServiceRepository: SearchServiceRepository,
) : StatisticsSearchRepository {
    override suspend fun searchStatistics() = searchServiceRepository.searchStatistics().map { it.toStatistics() }
}