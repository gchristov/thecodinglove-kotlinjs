package com.gchristov.thecodinglove.statistics.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.statistics.adapter.http.StatisticsHttpHandler
import com.gchristov.thecodinglove.statistics.adapter.search.RealSearchStatisticsRepository
import com.gchristov.thecodinglove.statistics.adapter.search.SearchStatisticsApi
import com.gchristov.thecodinglove.statistics.adapter.slack.RealSlackStatisticsRepository
import com.gchristov.thecodinglove.statistics.adapter.slack.SlackStatisticsApi
import com.gchristov.thecodinglove.statistics.domain.model.Environment
import com.gchristov.thecodinglove.statistics.domain.port.SearchStatisticsRepository
import com.gchristov.thecodinglove.statistics.domain.port.SlackStatisticsRepository
import com.gchristov.thecodinglove.statistics.domain.usecase.StatisticsReportUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object StatisticsAdapterModule : DiModule() {
    override fun name() = "statistics-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideStatisticsHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    statisticsReportUseCase = instance(),
                )
            }
            bindSingleton {
                provideSlackStatisticsApi(
                    networkClient = instance(),
                    environment = instance(),
                )
            }
            bindSingleton {
                provideSearchStatisticsApi(
                    networkClient = instance(),
                    environment = instance(),
                )
            }
            bindSingleton {
                provideSearchStatisticsRepository(
                    api = instance(),
                )
            }
            bindSingleton {
                provideSlackStatisticsRepository(
                    api = instance(),
                )
            }
        }
    }

    private fun provideStatisticsHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        statisticsReportUseCase: StatisticsReportUseCase,
    ): StatisticsHttpHandler = StatisticsHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        statisticsReportUseCase = statisticsReportUseCase,
    )

    private fun provideSearchStatisticsRepository(
        api: SearchStatisticsApi,
    ): SearchStatisticsRepository = RealSearchStatisticsRepository(
        apiService = api,
    )

    private fun provideSlackStatisticsRepository(
        api: SlackStatisticsApi,
    ): SlackStatisticsRepository = RealSlackStatisticsRepository(
        apiService = api,
    )

    private fun provideSlackStatisticsApi(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): SlackStatisticsApi = SlackStatisticsApi(
        client = networkClient,
        environment = environment,
    )

    private fun provideSearchStatisticsApi(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): SearchStatisticsApi = SearchStatisticsApi(
        client = networkClient,
        environment = environment,
    )
}