package com.gchristov.thecodinglove.statistics.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSearchRepository
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsSlackRepository
import com.gchristov.thecodinglove.statistics.domain.usecase.RealStatisticsReportUseCase
import com.gchristov.thecodinglove.statistics.domain.usecase.StatisticsReportUseCase
import kotlinx.coroutines.Dispatchers
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface StatisticsDomainComponent {
    @Provides
    @Singleton
    fun provideStatisticsReportUseCase(
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
