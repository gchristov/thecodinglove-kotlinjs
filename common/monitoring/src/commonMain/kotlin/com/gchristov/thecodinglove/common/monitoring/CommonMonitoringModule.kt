package com.gchristov.thecodinglove.common.monitoring

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.slack.SlackSender
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonMonitoringModule : DiModule() {
    override fun name() = "common-monitoring"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideMonitoringLogWriter(
                    slackSender = instance(),
                )
            }
        }
    }

    private fun provideMonitoringLogWriter(
        slackSender: SlackSender,
    ): MonitoringLogWriter = RealMonitoringLogWriter(
        dispatcher = Dispatchers.Default,
        slackSender = slackSender,
        monitoringSlackUrl = BuildConfig.MONITORING_SLACK_URL,
    )
}
