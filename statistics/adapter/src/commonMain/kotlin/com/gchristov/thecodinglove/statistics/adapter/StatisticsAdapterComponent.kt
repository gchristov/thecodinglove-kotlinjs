package com.gchristov.thecodinglove.statistics.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
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
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface StatisticsAdapterComponent {
    @Provides
    @Singleton
    fun provideStatisticsHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        statisticsReportUseCase: StatisticsReportUseCase,
    ): StatisticsHttpHandler = StatisticsHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        statisticsReportUseCase = statisticsReportUseCase,
    )

    @Provides
    @Singleton
    fun provideStatisticsSearchRepository(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): StatisticsSearchRepository = RealStatisticsSearchRepository(
        statisticsSearchServiceApi = StatisticsSearchServiceApi(
            client = networkClient,
            environment = environment,
        ),
    )

    @Provides
    @Singleton
    fun provideStatisticsSlackRepository(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): StatisticsSlackRepository = RealStatisticsSlackRepository(
        statisticsSlackServiceApi = StatisticsSlackServiceApi(
            client = networkClient,
            environment = environment,
        ),
    )
}
