package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackCancelSearchUseCase {
    suspend operator fun invoke(
        responseUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit>
}

class RealSlackCancelSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
    private val slackRepository: SlackRepository,
) : SlackCancelSearchUseCase {
    override suspend operator fun invoke(
        responseUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.d("Cancelling Slack message: responseUrl=$responseUrl")
        slackRepository.replyWithMessage(
            responseUrl = responseUrl,
            message = ApiSlackMessageFactory.cancelMessage()
        ).flatMap {
            log.d("Deleting search session: searchSessionId=$searchSessionId")
            searchRepository.deleteSearchSession(searchSessionId)
        }
    }
}