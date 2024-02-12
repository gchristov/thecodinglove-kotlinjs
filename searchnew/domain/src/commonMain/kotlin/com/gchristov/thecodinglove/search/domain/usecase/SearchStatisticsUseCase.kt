package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.domain.model.SearchStatistics
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

interface SearchStatisticsUseCase {
    suspend operator fun invoke() : Either<Throwable, SearchStatistics>
}

internal class RealSearchStatisticsUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
) : SearchStatisticsUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(): Either<Throwable, SearchStatistics> = withContext(dispatcher) {
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
            SearchStatistics(
                messagesSent = messagesSent.await().bind(),
                activeSearchSessions = activeSearchSessions.await().bind(),
                messagesSelfDestruct = messagesSelfDestruct.await().bind(),
            )
        }
    }
}