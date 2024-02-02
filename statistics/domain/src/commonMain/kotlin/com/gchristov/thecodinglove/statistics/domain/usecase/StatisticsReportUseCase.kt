package com.gchristov.thecodinglove.statistics.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.ports.StatisticsReportSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface StatisticsReportUseCase {
    suspend operator fun invoke(): Either<Throwable, StatisticsReport>
}

internal class RealStatisticsReportUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val statisticsReportSource: StatisticsReportSource,
) : StatisticsReportUseCase {
    private val tag = this::class.simpleName

    override suspend fun invoke(): Either<Throwable, StatisticsReport> = withContext(dispatcher) {
        val sentMessages = async {
            log.debug(tag, "Obtaining all sent messages")
            statisticsReportSource.getTotalSentMessages()
        }
        val activeSearchSessions = async {
            log.debug(tag, "Obtaining all active search sessions")
            statisticsReportSource.getTotalActiveSearchSessions()
        }
        val selfDestructMessages = async {
            log.debug(tag, "Obtaining all self-destruct search sessions")
            statisticsReportSource.getTotalSelfDestructMessages()
        }
        val slackActiveSelfDestructMessages = async {
            log.debug(tag, "Obtaining all active Slack self-destruct messages")
            statisticsReportSource.getTotalSlackActiveSelfDestructMessages()
        }
        val slackAuthTokens = async {
            log.debug(tag, "Obtaining all Slack auth tokens")
            statisticsReportSource.getTotalSlackUsers()
        }
        val slackTeams = async {
            log.debug(tag, "Obtaining all Slack teams")
            statisticsReportSource.getTotalSlackTeams()
        }
        either {
            StatisticsReport(
                messagesSent = sentMessages.await().bind(),
                activeSearchSessions = activeSearchSessions.await().bind(),
                messagesSelfDestruct = selfDestructMessages.await().bind(),
                slackActiveSelfDestructMessages = slackActiveSelfDestructMessages.await().bind(),
                slackUserTokens = slackAuthTokens.await().bind(),
                slackTeams = slackTeams.await().bind(),
            )
        }
    }
}