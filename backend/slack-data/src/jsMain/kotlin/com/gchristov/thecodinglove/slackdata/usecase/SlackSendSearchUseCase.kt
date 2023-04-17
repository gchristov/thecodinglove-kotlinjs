package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackSendSearchUseCase {
    suspend operator fun invoke(
        userId: String,
        teamId: String,
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
    private val slackConfig: SlackConfig,
) : SlackSendSearchUseCase {
    override suspend operator fun invoke(
        userId: String,
        teamId: String,
        channelId: String,
        responseUrl: String,
        searchSessionId: String
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.d("Checking auth token before sending message: userId=$userId")
        slackRepository.getAuthToken(tokenId = userId)
            .fold(
                ifLeft = {
                    log.e(it) { it.message ?: "Error fetching user token" }
                    authenticate(
                        userId = userId,
                        responseUrl = responseUrl,
                        searchSessionId = searchSessionId,
                        teamId = teamId,
                        clientId = slackConfig.clientId,
                    )
                },
                ifRight = {
                    sendResult(
                        authToken = it.token,
                        channelId = channelId,
                        responseUrl = responseUrl,
                        searchSessionId = searchSessionId
                    )
                }
            )
    }

    private suspend fun authenticate(
        userId: String,
        responseUrl: String,
        searchSessionId: String,
        teamId: String,
        clientId: String,
    ): Either<Throwable, Unit> {
        log.d("Asking user to authenticate: userId=$userId")
        return slackRepository.replyWithMessage(
            responseUrl = responseUrl,
            message = ApiSlackMessageFactory.authMessage(
                searchSessionId = searchSessionId,
                teamId = teamId,
                clientId = clientId,
            )
        )
    }

    private suspend fun sendResult(
        authToken: String,
        channelId: String,
        responseUrl: String,
        searchSessionId: String
    ): Either<Throwable, Unit> {
        log.d("Obtaining search session: searchSessionId=$searchSessionId")
        return searchRepository.getSearchSession(searchSessionId)
            .flatMap { searchSession ->
                log.d("Cancelling previous Slack message: responseUrl=$responseUrl")
                slackRepository.replyWithMessage(
                    responseUrl = responseUrl,
                    message = ApiSlackMessageFactory.cancelMessage()
                ).flatMap {
                    log.d("Posting search result: searchSessionId=$searchSessionId")
                    slackRepository.postMessage(
                        authToken = authToken,
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