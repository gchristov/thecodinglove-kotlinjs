package com.gchristov.thecodinglove.monitoringdata

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MonitoringLogWriter(
    private val dispatcher: CoroutineDispatcher,
    private val slackRepository: SlackRepository,
    private val slackConfig: SlackConfig,
) : LogWriter(), CoroutineScope {
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
            Severity.Assert -> launch(dispatcher) {
                val attachment = throwable?.let {
                    ApiSlackMessageFactory.attachment(
                        text = it.stackTraceToString(),
                        color = "#D00000",
                    )
                }
                val slackMessage = ApiSlackMessageFactory.message(
                    text = message,
                    attachments = listOfNotNull(attachment),
                )
                slackRepository.postMessageToUrl(
                    url = slackConfig.monitoringUrl,
                    message = slackMessage,
                ).fold(
                    ifLeft = {
                        // The logger has already attempted to post to Slack but has failed, so just log the error locally.
                        it.printStackTrace()
                    },
                    ifRight = {
                        // No-op
                    }
                )
            }
        }
    }
}
