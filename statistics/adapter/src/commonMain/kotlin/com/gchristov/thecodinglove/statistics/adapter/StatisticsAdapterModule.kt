package com.gchristov.thecodinglove.statistics.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.search.proto.http.SearchServiceRepository
import com.gchristov.thecodinglove.slack.proto.http.SlackServiceRepository
import com.gchristov.thecodinglove.statistics.adapter.http.StatisticsHttpHandler
import com.gchristov.thecodinglove.statistics.adapter.search.RealStatisticsSearchRepository
import com.gchristov.thecodinglove.statistics.adapter.slack.RealStatisticsSlackRepository
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
                )
            }
            bindSingleton {
                provideStatisticsSearchRepository(
                    searchServiceRepository = instance(),
                )
            }
            bindSingleton {
                provideStatisticsSlackRepository(
                    slackServiceRepository = instance(),
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

    private fun provideStatisticsSearchRepository(
        searchServiceRepository: SearchServiceRepository,
    ): StatisticsSearchRepository = RealStatisticsSearchRepository(
        searchServiceRepository = searchServiceRepository,
    )

    private fun provideStatisticsSlackRepository(
        slackServiceRepository: SlackServiceRepository,
    ): StatisticsSlackRepository = RealStatisticsSlackRepository(
        slackServiceRepository = slackServiceRepository,
    )
}