package com.gchristov.thecodinglove.statisticsdata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.statisticsdata.usecase.RealStatisticsReportUseCase
import com.gchristov.thecodinglove.statisticsdata.usecase.StatisticsReportUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object StatisticsDataModule : DiModule() {
    override fun name() = "statistics-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideStatisticsReportUseCase(
                    log = instance(),
                    searchRepository = instance(),
                    slackRepository = instance(),
                )
            }
        }
    }

    private fun provideStatisticsReportUseCase(
        log: Logger,
        searchRepository: SearchRepository,
        slackRepository: SlackRepository,
    ): StatisticsReportUseCase = RealStatisticsReportUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchRepository = searchRepository,
        slackRepository = slackRepository,
    )
}