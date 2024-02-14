package com.gchristov.thecodinglove.common.monitoring

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.monitoring.domain.MonitoringEnvironment
import com.gchristov.thecodinglove.common.monitoring.slack.RealSlackReportExceptionRepository
import com.gchristov.thecodinglove.common.monitoring.slack.SlackReportExceptionApi
import com.gchristov.thecodinglove.common.monitoring.slack.SlackReportExceptionRepository
import com.gchristov.thecodinglove.common.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

data class CommonMonitoringModule(val environment: MonitoringEnvironment) : DiModule() {
    override fun name() = "common-monitoring"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackReportExceptionApi(
                    networkClient = instance(),
                    environment = environment,
                )
            }
            bindSingleton {
                provideSlackReportExceptionRepository(
                    slackReportExceptionApi = instance(),
                )
            }
            bindSingleton {
                provideMonitoringLogWriter(
                    slackReportExceptionRepository = instance(),
                )
            }
        }
    }

    private fun provideSlackReportExceptionApi(
        networkClient: NetworkClient.Json,
        environment: MonitoringEnvironment,
    ): SlackReportExceptionApi = SlackReportExceptionApi(
        client = networkClient,
        environment = environment,
    )

    private fun provideSlackReportExceptionRepository(
        slackReportExceptionApi: SlackReportExceptionApi,
    ): SlackReportExceptionRepository = RealSlackReportExceptionRepository(
        slackReportExceptionApi = slackReportExceptionApi,
    )

    private fun provideMonitoringLogWriter(
        slackReportExceptionRepository: SlackReportExceptionRepository,
    ): MonitoringLogWriter = MonitoringLogWriter(
        dispatcher = Dispatchers.Default,
        slackReportExceptionRepository = slackReportExceptionRepository,
    )
}