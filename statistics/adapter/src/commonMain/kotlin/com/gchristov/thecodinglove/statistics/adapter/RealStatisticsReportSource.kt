package com.gchristov.thecodinglove.statistics.adapter

import arrow.core.Either
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.statistics.domain.port.StatisticsReportSource

class RealStatisticsReportSource(
    private val slackRepository: SlackRepository,
    private val searchRepository: SearchRepository,
) : StatisticsReportSource {
    override suspend fun getTotalSentMessages(): Either<Throwable, Int> =
        searchRepository.findSearchSessions(SearchSession.State.Sent()).map { it.size }

    override suspend fun getTotalActiveSearchSessions(): Either<Throwable, Int> =
        searchRepository.findSearchSessions(SearchSession.State.Searching()).map { it.size }

    override suspend fun getTotalSelfDestructMessages(): Either<Throwable, Int> =
        searchRepository.findSearchSessions(SearchSession.State.SelfDestruct()).map { it.size }

    override suspend fun getTotalSlackActiveSelfDestructMessages(): Either<Throwable, Int> =
        slackRepository.getSelfDestructMessages().map { it.size }

    override suspend fun getTotalSlackUsers(): Either<Throwable, Int> = slackRepository.getAuthTokens().map { it.size }

    override suspend fun getTotalSlackTeams(): Either<Throwable, Int> = slackRepository
        .getAuthTokens()
        .map { tokens ->
            val teams = mutableSetOf<String>()
            tokens.forEach {
                teams.add(it.teamId)
            }
            teams.size
        }
}