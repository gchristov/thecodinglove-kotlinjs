package com.gchristov.thecodinglove.common.monitoring

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slack.proto.http.SlackServiceRepository
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
                    slackServiceRepository = instance(),
                )
            }
        }
    }

    private fun provideMonitoringLogWriter(
        slackServiceRepository: SlackServiceRepository,
    ): MonitoringLogWriter = MonitoringLogWriter(
        dispatcher = Dispatchers.Default,
        slackServiceRepository = slackServiceRepository,
    )
}