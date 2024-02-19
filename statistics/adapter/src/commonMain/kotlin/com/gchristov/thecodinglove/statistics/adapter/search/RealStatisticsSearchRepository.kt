package com.gchristov.thecodinglove.statistics.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.adapter.search.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.adapter.search.model.ApiStatisticsSearch
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository
import io.ktor.client.call.*

internal class RealStatisticsSearchRepository(
    private val statisticsSearchServiceApi: StatisticsSearchServiceApi,
) : StatisticsSearchRepository {
    override suspend fun searchStatistics() = try {
        val response: ApiStatisticsSearch = statisticsSearchServiceApi.statistics().body()
        Either.Right(response.toStatistics())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}