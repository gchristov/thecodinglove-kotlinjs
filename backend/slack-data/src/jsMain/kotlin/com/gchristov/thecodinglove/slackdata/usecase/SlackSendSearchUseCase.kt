package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackSendSearchUseCase {
    suspend operator fun invoke(
        channelId: String,
        responseUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit>
}

class RealSlackSendSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
    private val slackRepository: SlackRepository,
) : SlackSendSearchUseCase {
    override suspend operator fun invoke(
        channelId: String,
        responseUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.d("Obtaining search session: searchSessionId=$searchSessionId")
        searchRepository.getSearchSession(searchSessionId)
            .flatMap { searchSession ->
                log.d("Cancelling previous Slack message: responseUrl=$responseUrl")
                slackRepository.replyWithMessage(
                    responseUrl = responseUrl,
                    message = ApiSlackMessageFactory.cancelMessage()
                ).flatMap {
                    log.d("Posting search result: searchSessionId=$searchSessionId")
                    slackRepository.postMessage(
                        authToken = "TOKEN",
                        message = ApiSlackMessageFactory.searchPostMessage(
                            searchQuery = searchSession.query,
                            attachmentTitle = searchSession.currentPost!!.title,
                            attachmentUrl = searchSession.currentPost!!.url,
                            attachmentImageUrl = searchSession.currentPost!!.imageUrl,
                            channelId = channelId,
                        )
                    ).flatMap {
                        // TODO: Should we track session state here?
                        log.d("Deleting search session: searchSessionId=$searchSessionId")
                        searchRepository.deleteSearchSession(searchSessionId)
                    }
                }
            }
    }
}