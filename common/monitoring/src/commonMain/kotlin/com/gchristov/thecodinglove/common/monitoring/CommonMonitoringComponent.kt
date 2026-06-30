package com.gchristov.thecodinglove.common.monitoring

import com.gchristov.thecodinglove.common.slack.SlackSender
import kotlinx.coroutines.Dispatchers
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonMonitoringComponent {
    @Provides
    @Singleton
    fun provideMonitoringLogWriter(slackSender: SlackSender): MonitoringLogWriter =
        RealMonitoringLogWriter(
            dispatcher = Dispatchers.Default,
            slackSender = slackSender,
            monitoringSlackUrl = BuildConfig.MONITORING_SLACK_URL,
        )
}
