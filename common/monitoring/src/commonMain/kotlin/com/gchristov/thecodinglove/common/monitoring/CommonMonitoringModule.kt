package com.gchristov.thecodinglove.common.monitoring

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.monitoring.slack.MonitoringSlackRepository
import com.gchristov.thecodinglove.common.monitoring.slack.MonitoringSlackServiceApi
import com.gchristov.thecodinglove.common.monitoring.slack.RealMonitoringSlackRepository
import com.gchristov.thecodinglove.common.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class CommonMonitoringModule(private val apiUrl: String) : DiModule() {
    override fun name() = "common-monitoring"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideMonitoringSlackServiceApi(
                    networkClient = instance(),
                    apiUrl = apiUrl,
                )
            }
            bindSingleton {
                provideMonitoringSlackRepository(
                    monitoringSlackServiceApi = instance(),
                )
            }
            bindSingleton {
                provideMonitoringLogWriter(
                    monitoringSlackRepository = instance(),
                )
            }
        }
    }

    private fun provideMonitoringSlackServiceApi(
        networkClient: NetworkClient.Json,
        apiUrl: String,
    ): MonitoringSlackServiceApi = MonitoringSlackServiceApi(
        client = networkClient,
        apiUrl = apiUrl,
    )

    private fun provideMonitoringSlackRepository(
        monitoringSlackServiceApi: MonitoringSlackServiceApi,
    ): MonitoringSlackRepository = RealMonitoringSlackRepository(
        monitoringSlackServiceApi = monitoringSlackServiceApi,
    )

    private fun provideMonitoringLogWriter(
        monitoringSlackRepository: MonitoringSlackRepository,
    ): MonitoringLogWriter = RealMonitoringLogWriter(
        dispatcher = Dispatchers.Default,
        monitoringSlackRepository = monitoringSlackRepository,
    )
}