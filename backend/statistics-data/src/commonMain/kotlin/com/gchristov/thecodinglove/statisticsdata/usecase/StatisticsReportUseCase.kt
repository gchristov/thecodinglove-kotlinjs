package com.gchristov.thecodinglove.statisticsdata.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.debug
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackAuthToken
import com.gchristov.thecodinglove.statisticsdata.domain.StatisticsReport
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
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
        val sentMessages = async {
            log.debug(tag, "Obtaining all sent search sessions")
            searchRepository.findSearchSessions(SearchSession.State.Sent())
        }
        val activeSearchSessions = async {
            log.debug(tag, "Obtaining all active search sessions")
            searchRepository.findSearchSessions(SearchSession.State.Searching())
        }
        val selfDestructMessages = async {
            log.debug(tag, "Obtaining all self-destruct search sessions")
            searchRepository.findSearchSessions(SearchSession.State.SelfDestruct())
        }
        val slackActiveSelfDestructMessages = async {
            log.debug(tag, "Obtaining all active Slack self-destruct messages")
            slackRepository.getSelfDestructMessages()
        }
        val slackAuthTokens = async {
            log.debug(tag, "Obtaining all Slack auth tokens")
            slackRepository.getAuthTokens()
        }
        either {
            StatisticsReport(
                messagesSent = sentMessages.await().bind().size,
                activeSearchSessions = activeSearchSessions.await().bind().size,
                messagesSelfDestruct = selfDestructMessages.await().bind().size,
                slackActiveSelfDestructMessages = slackActiveSelfDestructMessages.await().bind().size,
                slackUserTokens = slackAuthTokens.await().bind().size,
                slackTeams = slackAuthTokens.await().bind().teamsCount(),
            )
        }
    }
}

private fun List<SlackAuthToken>.teamsCount(): Int {
    val teams = mutableSetOf<String>()
    forEach {
        teams.add(it.teamId)
    }
    return teams.size
}