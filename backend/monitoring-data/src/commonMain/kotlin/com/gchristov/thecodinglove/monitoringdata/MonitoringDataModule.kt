package com.gchristov.thecodinglove.monitoringdata

import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object MonitoringDataModule : DiModule() {
    override fun name() = "monitoring-data"

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