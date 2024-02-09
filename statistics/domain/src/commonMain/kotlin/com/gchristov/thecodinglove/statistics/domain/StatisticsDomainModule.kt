package com.gchristov.thecodinglove.statistics.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.statistics.domain.port.SearchStatisticsRepository
import com.gchristov.thecodinglove.statistics.domain.port.SlackStatisticsRepository
import com.gchristov.thecodinglove.statistics.domain.usecase.RealStatisticsReportUseCase
import com.gchristov.thecodinglove.statistics.domain.usecase.StatisticsReportUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object StatisticsDomainModule : DiModule() {
    override fun name() = "statistics-domain"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideStatisticsReportUseCase(
                    log = instance(),
                    searchStatisticsRepository = instance(),
                    slackStatisticsRepository = instance(),
                )
            }
        }
    }

    private fun provideStatisticsReportUseCase(
        log: Logger,
        searchStatisticsRepository: SearchStatisticsRepository,
        slackStatisticsRepository: SlackStatisticsRepository,
    ): StatisticsReportUseCase = RealStatisticsReportUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchStatisticsRepository = searchStatisticsRepository,
        slackStatisticsRepository = slackStatisticsRepository,
    )
}