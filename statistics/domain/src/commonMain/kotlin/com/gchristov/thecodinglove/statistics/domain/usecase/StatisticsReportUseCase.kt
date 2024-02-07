package com.gchristov.thecodinglove.statistics.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.SearchStatisticsRepository
import com.gchristov.thecodinglove.statistics.domain.port.SlackStatisticsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface StatisticsReportUseCase {
    suspend operator fun invoke(): Either<Throwable, StatisticsReport>
}

internal class RealStatisticsReportUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchStatisticsRepository: SearchStatisticsRepository,
    private val slackStatisticsRepository: SlackStatisticsRepository,
) : StatisticsReportUseCase {
    private val tag = this::class.simpleName

    override suspend fun invoke(): Either<Throwable, StatisticsReport> = withContext(dispatcher) {
        val searchStatistics = async {
            log.debug(tag, "Obtaining search statistics")
            searchStatisticsRepository.statistics()
        }
        val slackStatistics = async {
            log.debug(tag, "Obtaining Slack statistics")
            slackStatisticsRepository.statistics()
        }
        either {
            StatisticsReport(
                searchStatistics = searchStatistics.await().bind(),
                slackStatistics = slackStatistics.await().bind(),
            )
        }
    }
}