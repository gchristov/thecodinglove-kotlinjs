package com.gchristov.thecodinglove.statistics.proto.http

import arrow.core.Either
import com.gchristov.thecodinglove.statistics.proto.http.model.ApiStatisticsReport
import io.ktor.client.call.*

interface StatisticsServiceRepository {
    suspend fun statistics(): Either<Throwable, ApiStatisticsReport>
}

internal class RealStatisticsServiceRepository(
    private val statisticsServiceApi: StatisticsServiceApi,
) : StatisticsServiceRepository {
    override suspend fun statistics() = try {
        val response: ApiStatisticsReport = statisticsServiceApi.selfDestruct().body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}