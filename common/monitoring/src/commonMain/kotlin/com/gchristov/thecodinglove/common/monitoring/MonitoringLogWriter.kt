package com.gchristov.thecodinglove.common.monitoring

import arrow.core.getOrElse
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.gchristov.thecodinglove.common.slack.SlackSender
import com.gchristov.thecodinglove.common.slack.model.SlackMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class MonitoringLogWriter : LogWriter()

internal class RealMonitoringLogWriter(
    dispatcher: CoroutineDispatcher,
    private val slackSender: SlackSender,
    private val monitoringSlackUrl: String,
) : MonitoringLogWriter() {
    private val scope = CoroutineScope(dispatcher)

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
    ) = scope.launch {
        val stacktrace = throwable?.stackTraceToString() ?: "Missing stacktrace"
        slackSender.postMessageToUrl(
            url = monitoringSlackUrl,
            message = SlackMessage(
                text = message,
                attachments = listOf(
                    SlackMessage.Attachment(
                        text = stacktrace,
                        color = "#D00000",
                    )
                ),
            ),
        ).getOrElse {
            // The logger has already attempted to post to Slack but has failed, so just log the error locally.
            it.printStackTrace()
            return@launch
        }
    }
}
