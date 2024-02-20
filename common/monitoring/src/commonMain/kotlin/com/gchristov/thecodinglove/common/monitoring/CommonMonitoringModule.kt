package com.gchristov.thecodinglove.common.monitoring

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.monitoring.slack.MonitoringSlackApi
import com.gchristov.thecodinglove.common.monitoring.slack.MonitoringSlackRepository
import com.gchristov.thecodinglove.common.monitoring.slack.RealMonitoringSlackRepository
import com.gchristov.thecodinglove.common.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonMonitoringModule : DiModule() {
    override fun name() = "common-monitoring"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideMonitoringSlackApi(
                    networkClient = instance(),
                )
            }
            bindSingleton {
                provideMonitoringSlackRepository(
                    monitoringSlackApi = instance(),
                )
            }
            bindSingleton {
                provideMonitoringLogWriter(
                    monitoringSlackRepository = instance(),
                )
            }
        }
    }

    private fun provideMonitoringSlackApi(
        networkClient: NetworkClient.Json,
    ): MonitoringSlackApi = MonitoringSlackApi(
        client = networkClient,
        monitoringSlackUrl = BuildConfig.MONITORING_SLACK_URL,
    )

    private fun provideMonitoringSlackRepository(
        monitoringSlackApi: MonitoringSlackApi,
    ): MonitoringSlackRepository = RealMonitoringSlackRepository(
        monitoringSlackApi = monitoringSlackApi,
    )

    private fun provideMonitoringLogWriter(
        monitoringSlackRepository: MonitoringSlackRepository,
    ): MonitoringLogWriter = RealMonitoringLogWriter(
        dispatcher = Dispatchers.Default,
        monitoringSlackRepository = monitoringSlackRepository,
    )
}