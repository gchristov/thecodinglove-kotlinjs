package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SendSlackSearchUseCase {
    suspend operator fun invoke(
        messageUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit>
}

class RealSendSlackSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
    private val slackRepository: SlackRepository,
) : SendSlackSearchUseCase {
    override suspend operator fun invoke(
        messageUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.d("Obtaining search session: searchSessionId=$searchSessionId")
        searchRepository.getSearchSession(searchSessionId)
            .flatMap { searchSession ->
                slackRepository.sendMessage(
                    messageUrl = messageUrl,
                    message = ApiSlackMessageFactory.searchPostMessage(
                        searchQuery = searchSession.query,
                        attachmentTitle = searchSession.currentPost!!.title,
                        attachmentUrl = searchSession.currentPost!!.url,
                        attachmentImageUrl = searchSession.currentPost!!.imageUrl
                    )
                ).flatMap {
                    log.d("Deleting search session: searchSessionId=$searchSessionId")
                    searchRepository.deleteSearchSession(searchSessionId)
                }
            }
    }
}