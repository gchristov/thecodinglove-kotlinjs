package com.gchristov.thecodinglove.common.monitoring

import arrow.core.raise.either
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.gchristov.thecodinglove.common.monitoring.slack.MonitoringSlackRepository
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class MonitoringLogWriter : LogWriter()

internal class RealMonitoringLogWriter(
    private val dispatcher: CoroutineDispatcher,
    private val monitoringSlackRepository: MonitoringSlackRepository,
) : MonitoringLogWriter(), CoroutineScope {
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?,
    ) {
        when (severity) {
            Severity.Verbose,
            Severity.Debug,
            Severity.Info -> return

            Severity.Warn,
            Severity.Error,
            Severity.Assert -> reportException(message, throwable)
        }
    }

    private fun reportException(
        message: String,
        throwable: Throwable?,
    ) = launch(dispatcher) {
        val stacktrace = throwable?.stackTraceToString() ?: "Missing stacktrace"
        val slack = async {
            reportToSlack(
                message = message,
                stacktrace = stacktrace,
            )
        }
        either {
            slack.await().bind()
        }.fold(
            ifLeft = {
                // The logger has already attempted to post to Slack but has failed, so just log the error locally.
                it.printStackTrace()
            },
            ifRight = {
                // No-op
            }
        )
    }

    private suspend fun reportToSlack(
        message: String,
        stacktrace: String,
    ) = monitoringSlackRepository.reportException(
        message = message,
        stacktrace = stacktrace,
    )
}
