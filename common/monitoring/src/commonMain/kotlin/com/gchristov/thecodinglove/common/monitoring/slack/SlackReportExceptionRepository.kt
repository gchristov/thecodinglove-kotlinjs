package com.gchristov.thecodinglove.common.monitoring.slack

import arrow.core.Either

interface SlackReportExceptionRepository {
    suspend fun reportException(
        message: String,
        stacktrace: String,
    ): Either<Throwable, Unit>
}

internal class RealSlackReportExceptionRepository(
    private val slackReportExceptionApi: SlackReportExceptionApi,
) : SlackReportExceptionRepository {
    override suspend fun reportException(
        message: String,
        stacktrace: String,
    ): Either<Throwable, Unit> = try {
        slackReportExceptionApi.reportException(
            ApiSlackReportException(
                message = message,
                stacktrace = stacktrace,
            )
        )
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during Slack report exception${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}