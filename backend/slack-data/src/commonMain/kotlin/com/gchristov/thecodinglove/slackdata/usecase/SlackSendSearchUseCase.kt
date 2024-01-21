package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.debug
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthState
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackSelfDestructMessage
import io.ktor.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.serialization.encodeToString

interface SlackSendSearchUseCase {
    suspend operator fun invoke(
        userId: String,
        teamId: String,
        channelId: String,
        responseUrl: String,
        searchSessionId: String,
        selfDestructMinutes: Int? = null,
    ): Either<Throwable, Unit>
}

class RealSlackSendSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchRepository: SearchRepository,
    private val slackRepository: SlackRepository,
    private val slackConfig: SlackConfig,
    private val jsonSerializer: JsonSerializer,
    private val clock: Clock,
) : SlackSendSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(
        userId: String,
        teamId: String,
        channelId: String,
        responseUrl: String,
        searchSessionId: String,
        selfDestructMinutes: Int?,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.debug(tag, "Checking auth token before sending message: userId=$userId")
        slackRepository.getAuthToken(tokenId = userId)
            .fold(
                ifLeft = { error ->
                    log.debug(tag, error) { "Error fetching user token${error.message?.let { ": $it" } ?: ""}" }
                    authenticate(
                        userId = userId,
                        responseUrl = responseUrl,
                        searchSessionId = searchSessionId,
                        teamId = teamId,
                        clientId = slackConfig.clientId,
                        channelId = channelId,
                        selfDestructMinutes = selfDestructMinutes,
                    )
                },
                ifRight = {
                    sendResult(
                        userId = userId,
                        authToken = it.token,
                        channelId = channelId,
                        responseUrl = responseUrl,
                        searchSessionId = searchSessionId,
                        selfDestructMinutes = selfDestructMinutes,
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
        channelId: String,
        selfDestructMinutes: Int?,
    ): Either<Throwable, Unit> = try {
        val state = jsonSerializer.json.encodeToString(
            ApiSlackAuthState(
                searchSessionId = searchSessionId,
                channelId = channelId,
                teamId = teamId,
                userId = userId,
                responseUrl = responseUrl,
                selfDestructMinutes = selfDestructMinutes,
            )
        ).encodeBase64()
        log.debug(tag, "Asking user to authenticate: userId=$userId, state=$state")
        slackRepository.postMessageToUrl(
            url = responseUrl,
            message = ApiSlackMessageFactory.authMessage(
                searchSessionId = searchSessionId,
                teamId = teamId,
                clientId = clientId,
                state = state,
            )
        )
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during user authentication${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    private suspend fun sendResult(
        userId: String,
        authToken: String,
        channelId: String,
        responseUrl: String,
        searchSessionId: String,
        selfDestructMinutes: Int?,
    ): Either<Throwable, Unit> {
        log.debug(tag, "Obtaining search session: searchSessionId=$searchSessionId")
        return searchRepository.getSearchSession(searchSessionId)
            .flatMap { searchSession ->
                log.debug(tag, "Cancelling previous search: responseUrl=$responseUrl")
                slackRepository.postMessageToUrl(
                    url = responseUrl,
                    message = ApiSlackMessageFactory.cancelMessage()
                ).flatMap {
                    log.debug(tag, "Posting search result: searchSessionId=$searchSessionId")
                    slackRepository.postMessage(
                        authToken = authToken,
                        message = ApiSlackMessageFactory.searchPostMessage(
                            searchQuery = searchSession.query,
                            attachmentTitle = searchSession.currentPost!!.title,
                            attachmentUrl = searchSession.currentPost!!.url,
                            attachmentImageUrl = searchSession.currentPost!!.imageUrl,
                            channelId = channelId,
                            selfDestructMinutes = selfDestructMinutes,
                        )
                    ).flatMap { messageTs ->
                        val logPlaceholder = selfDestructMinutes?.let { "self-destruct" } ?: "sent"
                        val state = selfDestructMinutes?.let {
                            SearchSession.State.SelfDestruct
                        } ?: SearchSession.State.Sent
                        log.debug(tag, "Marking search session as $logPlaceholder: searchSessionId=$searchSessionId")
                        searchRepository
                            .saveSearchSession(searchSession.copy(state = state))
                            .flatMap {
                                selfDestructMinutes?.let {
                                    log.debug(tag, "Persisting self-destruct state: searchSessionId=$searchSessionId")
                                    val destroyTimestamp = clock.now().plus(
                                        value = selfDestructMinutes,
                                        unit = DateTimeUnit.MINUTE,
                                    ).toEpochMilliseconds()
                                    slackRepository.saveSelfDestructMessage(
                                        SlackSelfDestructMessage(
                                            id = searchSessionId,
                                            userId = userId,
                                            searchSessionId = searchSessionId,
                                            destroyTimestamp = destroyTimestamp,
                                            channelId = channelId,
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