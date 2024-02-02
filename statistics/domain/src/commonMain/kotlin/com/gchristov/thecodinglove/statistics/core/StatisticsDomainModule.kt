package com.gchristov.thecodinglove.statistics.core

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.statistics.core.ports.StatisticsReportSource
import com.gchristov.thecodinglove.statistics.core.usecase.RealStatisticsReportUseCase
import com.gchristov.thecodinglove.statistics.core.usecase.StatisticsReportUseCase
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
                    statisticsReportSource = instance(),
                )
            }
        }
    }

    private fun provideStatisticsReportUseCase(
        log: Logger,
        statisticsReportSource: StatisticsReportSource,
    ): StatisticsReportUseCase = RealStatisticsReportUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        statisticsReportSource = statisticsReportSource,
    )
}