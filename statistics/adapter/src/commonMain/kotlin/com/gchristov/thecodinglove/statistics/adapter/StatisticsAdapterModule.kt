package com.gchristov.thecodinglove.statistics.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.statistics.adapter.http.StatisticsHttpHandler
import com.gchristov.thecodinglove.statistics.adapter.search.RealStatisticsSearchRepository
import com.gchristov.thecodinglove.statistics.adapter.search.StatisticsSearchServiceApi
import com.gchristov.thecodinglove.statistics.adapter.slack.RealStatisticsSlackRepository
import com.gchristov.thecodinglove.statistics.adapter.slack.StatisticsSlackServiceApi
import com.gchristov.thecodinglove.statistics.domain.model.Environment
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository
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
                    analytics = instance(),
                )
            }
            bindSingleton {
                provideStatisticsSearchServiceApi(
                    networkClient = instance(),
                    environment = instance(),
                )
            }
            bindSingleton {
                provideStatisticsSearchRepository(
                    statisticsSearchServiceApi = instance(),
                )
            }
            bindSingleton {
                provideStatisticsSlackServiceApi(
                    networkClient = instance(),
                    environment = instance(),
                )
            }
            bindSingleton {
                provideStatisticsSlackRepository(
                    statisticsSlackServiceApi = instance(),
                )
            }
        }
    }

    private fun provideStatisticsHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        statisticsReportUseCase: StatisticsReportUseCase,
        analytics: Analytics,
    ): StatisticsHttpHandler = StatisticsHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        statisticsReportUseCase = statisticsReportUseCase,
        analytics = analytics,
    )

    private fun provideStatisticsSearchServiceApi(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): StatisticsSearchServiceApi = StatisticsSearchServiceApi(
        client = networkClient,
        environment = environment,
    )

    private fun provideStatisticsSearchRepository(
        statisticsSearchServiceApi: StatisticsSearchServiceApi,
    ): StatisticsSearchRepository = RealStatisticsSearchRepository(
        statisticsSearchServiceApi = statisticsSearchServiceApi,
    )

    private fun provideStatisticsSlackServiceApi(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): StatisticsSlackServiceApi = StatisticsSlackServiceApi(
        client = networkClient,
        environment = environment,
    )

    private fun provideStatisticsSlackRepository(
        statisticsSlackServiceApi: StatisticsSlackServiceApi,
    ): StatisticsSlackRepository = RealStatisticsSlackRepository(
        statisticsSlackServiceApi = statisticsSlackServiceApi,
    )
}