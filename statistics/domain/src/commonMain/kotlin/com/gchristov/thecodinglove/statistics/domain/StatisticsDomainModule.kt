package com.gchristov.thecodinglove.statistics.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository
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
                    statisticsSearchRepository = instance(),
                    statisticsSlackRepository = instance(),
                )
            }
        }
    }

    private fun provideStatisticsReportUseCase(
        log: Logger,
        statisticsSearchRepository: StatisticsSearchRepository,
        statisticsSlackRepository: StatisticsSlackRepository,
    ): StatisticsReportUseCase = RealStatisticsReportUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        statisticsSearchRepository = statisticsSearchRepository,
        statisticsSlackRepository = statisticsSlackRepository,
    )
}