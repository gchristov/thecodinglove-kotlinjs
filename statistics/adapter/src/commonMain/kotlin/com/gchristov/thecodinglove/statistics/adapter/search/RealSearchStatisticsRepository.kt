package com.gchristov.thecodinglove.statistics.adapter.search

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.adapter.search.mapper.toStatistics
import com.gchristov.thecodinglove.statistics.adapter.search.model.ApiSearchStatistics
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.SearchStatisticsRepository
import io.ktor.client.call.*

internal class RealSearchStatisticsRepository(
    private val searchStatisticsApi: SearchStatisticsApi,
) : SearchStatisticsRepository {
    override suspend fun statistics(): Either<Throwable, StatisticsReport.SearchStatistics> = try {
        val response: ApiSearchStatistics = searchStatisticsApi.statistics().body()
        Either.Right(response.toStatistics())
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}