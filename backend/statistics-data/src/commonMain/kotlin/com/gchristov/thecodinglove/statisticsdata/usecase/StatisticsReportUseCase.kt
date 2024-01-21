package com.gchristov.thecodinglove.statisticsdata.usecase

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.statisticsdata.domain.StatisticsReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface StatisticsReportUseCase {
    suspend operator fun invoke(): Either<Throwable, StatisticsReport>
}

internal class RealStatisticsReportUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val searchRepository: SearchRepository,
) : StatisticsReportUseCase {
    private val tag = this::class.simpleName

    override suspend fun invoke(): Either<Throwable, StatisticsReport> = withContext(dispatcher) {
        Either.Right(
            StatisticsReport(
                messagesSent = 0,
                messagesSearching = 0,
                messagesSelfDestruct = 0,
                activeSelfDestructMessages = 0,
                userTokens = 0,
                teams = 0,
            )
        )
    }
}