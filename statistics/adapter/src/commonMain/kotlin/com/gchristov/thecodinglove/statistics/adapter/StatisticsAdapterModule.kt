package com.gchristov.thecodinglove.statistics.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.statistics.adapter.http.StatisticsHttpHandler
import com.gchristov.thecodinglove.statistics.core.ports.StatisticsReportSource
import com.gchristov.thecodinglove.statistics.core.usecase.StatisticsReportUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
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
            bindProvider {
                provideStatisticsReportSource(
                    slackRepository = instance(),
                    searchRepository = instance(),
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

    private fun provideStatisticsReportSource(
        slackRepository: SlackRepository,
        searchRepository: SearchRepository,
    ): StatisticsReportSource = RealStatisticsReportSource(
        slackRepository = slackRepository,
        searchRepository = searchRepository,
    )
}