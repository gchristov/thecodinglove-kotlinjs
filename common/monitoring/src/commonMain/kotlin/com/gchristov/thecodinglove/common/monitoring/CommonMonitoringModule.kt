package com.gchristov.thecodinglove.common.monitoring

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
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
                    slackRepository = instance(),
                    slackConfig = instance(),
                )
            }
        }
    }

    private fun provideMonitoringLogWriter(
        slackRepository: SlackRepository,
        slackConfig: SlackConfig,
    ): MonitoringLogWriter = MonitoringLogWriter(
        dispatcher = Dispatchers.Default,
        slackRepository = slackRepository,
        slackConfig = slackConfig,
    )
}