package com.gchristov.thecodinglove.common.monitoring.slack

import arrow.core.Either
import com.gchristov.thecodinglove.common.monitoring.slack.model.ApiMonitoringReportExceptionSlack

internal interface MonitoringSlackRepository {
    suspend fun reportException(exception: ApiMonitoringReportExceptionSlack): Either<Throwable, Unit>
}

internal class RealMonitoringSlackRepository(
    private val monitoringSlackServiceApi: MonitoringSlackServiceApi,
) : MonitoringSlackRepository {
    override suspend fun reportException(exception: ApiMonitoringReportExceptionSlack) = try {
        monitoringSlackServiceApi.reportException(exception)
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during report exception${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}