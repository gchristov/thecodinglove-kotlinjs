package com.gchristov.thecodinglove.statistics.adapter.search

import com.gchristov.thecodinglove.common.network.safeApiCall
import com.gchristov.thecodinglove.statistics.adapter.search.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.adapter.search.model.ApiStatisticsSearch
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository
import io.ktor.client.call.*

internal class RealStatisticsSearchRepository(
    private val statisticsSearchServiceApi: StatisticsSearchServiceApi,
) : StatisticsSearchRepository {
    override suspend fun searchStatistics() = safeApiCall("Error during statistics") {
        statisticsSearchServiceApi.statistics().body<ApiStatisticsSearch>().toStatistics()
    }
}
