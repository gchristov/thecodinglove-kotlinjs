package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
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

internal class RealSlackCancelSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
    private val slackRepository: SlackRepository,
) : SlackCancelSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(
        responseUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.debug(tag, "Cancelling search: responseUrl=$responseUrl")
        slackRepository.postMessageToUrl(
            url = responseUrl,
            message = ApiSlackMessageFactory.cancelMessage()
        ).flatMap {
            log.debug(tag, "Deleting search session: searchSessionId=$searchSessionId")
            searchRepository.deleteSearchSession(searchSessionId)
        }
    }
}