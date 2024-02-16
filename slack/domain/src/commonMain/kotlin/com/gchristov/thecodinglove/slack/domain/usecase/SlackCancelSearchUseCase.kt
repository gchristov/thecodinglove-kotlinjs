package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SearchRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackCancelSearchUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Unit>

    data class Dto(
        val responseUrl: String,
        val searchSessionId: String,
    )
}

internal class RealSlackCancelSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackRepository: SlackRepository,
) : SlackCancelSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(dto: SlackCancelSearchUseCase.Dto): Either<Throwable, Unit> =
        withContext(dispatcher) {
            log.debug(tag, "Cancelling search: responseUrl=${dto.responseUrl}")
            slackRepository.postMessageToUrl(
                url = dto.responseUrl,
                message = slackMessageFactory.cancelMessage(),
            ).flatMap {
                log.debug(tag, "Deleting search session: searchSessionId=${dto.searchSessionId}")
                searchRepository.deleteSearchSession(dto.searchSessionId)
            }
        }
}