package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.port.SearchRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

interface SlackSendSearchUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Unit>

    data class Dto(
        val userId: String,
        val teamId: String,
        val channelId: String,
        val responseUrl: String,
        val searchSessionId: String,
        val selfDestructMinutes: Int? = null,
    )
}

internal class RealSlackSendSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackConfig: SlackConfig,
    private val clock: Clock,
) : SlackSendSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(dto: SlackSendSearchUseCase.Dto): Either<Throwable, Unit> =
        withContext(dispatcher) {
            log.debug(tag, "Checking auth token before sending message: userId=${dto.userId}")
            val authState = SlackAuthState(
                searchSessionId = dto.searchSessionId,
                channelId = dto.channelId,
                teamId = dto.teamId,
                userId = dto.userId,
                responseUrl = dto.responseUrl,
                selfDestructMinutes = dto.selfDestructMinutes,
            )
            slackRepository.getAuthToken(tokenId = dto.userId)
                .fold(
                    ifLeft = { error ->
                        log.debug(tag, error) { "Error fetching user token${error.message?.let { ": $it" } ?: ""}" }
                        authenticate(
                            clientId = slackConfig.clientId,
                            authState = authState,
                        )
                    },
                    ifRight = {
                        sendResult(
                            authState = authState,
                            authToken = it.token,
                        )
                    }
                )
        }

    private suspend fun authenticate(
        clientId: String,
        authState: SlackAuthState,
    ): Either<Throwable, Unit> = try {
        log.debug(tag, "Asking user to authenticate: userId=${authState.userId}")
        slackRepository.postMessageToUrl(
            url = authState.responseUrl,
            message = slackMessageFactory.authMessage(
                clientId = clientId,
                authState = authState,
            )
        )
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during user authentication${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    private suspend fun sendResult(
        authState: SlackAuthState,
        authToken: String,
    ): Either<Throwable, Unit> {
        log.debug(tag, "Obtaining search session: searchSessionId=${authState.searchSessionId}")
        return searchRepository.getSearchSessionPost(authState.searchSessionId)
            .flatMap { searchSessionPost ->
                log.debug(tag, "Cancelling previous search: responseUrl=${authState.responseUrl}")
                slackRepository.postMessageToUrl(
                    url = authState.responseUrl,
                    message = slackMessageFactory.cancelMessage()
                ).flatMap {
                    log.debug(tag, "Posting search result: searchSessionId=${authState.searchSessionId}")
                    slackRepository.postMessage(
                        authToken = authToken,
                        message = slackMessageFactory.searchPostMessage(
                            searchQuery = searchSessionPost.searchQuery,
                            attachmentTitle = searchSessionPost.attachmentTitle,
                            attachmentUrl = searchSessionPost.attachmentUrl,
                            attachmentImageUrl = searchSessionPost.attachmentImageUrl,
                            channelId = authState.channelId,
                            selfDestructMinutes = authState.selfDestructMinutes,
                        )
                    ).flatMap { messageTs ->
                        val logPlaceholder = authState.selfDestructMinutes?.let { "self-destruct" } ?: "sent"
                        val state = authState.selfDestructMinutes?.let {
                            SearchRepository.SearchSessionStateDto.SelfDestruct
                        } ?: SearchRepository.SearchSessionStateDto.Sent
                        log.debug(
                            tag,
                            "Marking search session as $logPlaceholder: searchSessionId=${authState.searchSessionId}"
                        )
                        searchRepository
                            .updateSearchSessionState(
                                searchSessionId = authState.searchSessionId,
                                state = state,
                            )
                            .flatMap {
                                authState.selfDestructMinutes?.let {
                                    log.debug(
                                        tag,
                                        "Persisting self-destruct state: searchSessionId=${authState.searchSessionId}"
                                    )
                                    val destroyTimestamp = clock.now().plus(
                                        value = it,
                                        unit = DateTimeUnit.MINUTE,
                                    ).toEpochMilliseconds()
                                    slackRepository.saveSelfDestructMessage(
                                        SlackSelfDestructMessage(
                                            id = authState.searchSessionId,
                                            userId = authState.userId,
                                            searchSessionId = authState.searchSessionId,
                                            destroyTimestamp = destroyTimestamp,
                                            channelId = authState.channelId,
                                            messageTs = messageTs,
                                        )
                                    )
                                } ?: Either.Right(Unit)
                            }
                    }
                }
            }
    }
}