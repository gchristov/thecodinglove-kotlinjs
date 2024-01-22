package com.gchristov.thecodinglove.statisticsdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.debug
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackAuthToken
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
        log.debug(tag, "Obtaining all active self-destruct messages")
        slackRepository.getSelfDestructMessages()
            .flatMap { activeSelfDestructMessages ->
                log.debug(tag, "Obtaining all sent search sessions")
                searchRepository.findSearchSessions(SearchSession.State.Sent())
                    .flatMap { sentMessages ->
                        log.debug(tag, "Obtaining all active search sessions")
                        searchRepository.findSearchSessions(SearchSession.State.Searching())
                            .flatMap { activeSearchSessions ->
                                log.debug(tag, "Obtaining all self-destruct search sessions")
                                searchRepository.findSearchSessions(SearchSession.State.SelfDestruct())
                                    .flatMap { selfDestructMessages ->
                                        log.debug(tag, "Obtaining all Slack auth tokens")
                                        slackRepository.getAuthTokens()
                                            .flatMap { authTokens ->
                                                Either.Right(
                                                    StatisticsReport(
                                                        messagesSent = sentMessages.size,
                                                        activeSearchSessions = activeSearchSessions.size,
                                                        messagesSelfDestruct = selfDestructMessages.size,
                                                        activeSelfDestructMessages = activeSelfDestructMessages.size,
                                                        userTokens = authTokens.size,
                                                        teams = authTokens.teamsCount(),
                                                    )
                                                )
                                            }
                                    }
                            }
                    }
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