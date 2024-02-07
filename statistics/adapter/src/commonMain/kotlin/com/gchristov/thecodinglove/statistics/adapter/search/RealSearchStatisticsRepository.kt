package com.gchristov.thecodinglove.statistics.adapter.search

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.domain.port.SearchStatisticsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

internal class RealSearchStatisticsRepository(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
) : SearchStatisticsRepository {
    private val tag = this::class.simpleName

    override suspend fun statistics(): Either<Throwable, StatisticsReport.SearchStatistics> = withContext(dispatcher) {
        val messagesSent = async {
            log.debug(tag, "Obtaining sent messages")
            searchRepository.findSearchSessions(SearchSession.State.Sent()).map { it.size }
        }
        val activeSearchSessions = async {
            log.debug(tag, "Obtaining active search sessions statistics")
            searchRepository.findSearchSessions(SearchSession.State.Searching()).map { it.size }
        }
        val messagesSelfDestruct = async {
            log.debug(tag, "Obtaining self-destruct messages")
            searchRepository.findSearchSessions(SearchSession.State.SelfDestruct()).map { it.size }
        }
        either {
            StatisticsReport.SearchStatistics(
                messagesSent = messagesSent.await().bind(),
                activeSearchSessions = activeSearchSessions.await().bind(),
                messagesSelfDestruct = messagesSelfDestruct.await().bind(),
            )
        }
    }
}