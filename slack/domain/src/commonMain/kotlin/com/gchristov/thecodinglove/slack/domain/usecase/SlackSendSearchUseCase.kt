package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage
import com.gchristov.thecodinglove.slack.domain.model.isSelfDestruct
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackSendSearchUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, SlackSentMessage>

    sealed class Error(message: String? = null) : Throwable(message) {
        data object NotAuthenticated : Error("User is not authenticated")
    }

    data class Dto(
        val userId: String,
        val teamId: String,
        val channelId: String,
        val responseUrl: String,
        val searchSessionId: String,
        val selfDestructSeconds: Long? = null,
    )
}

@OptIn(ExperimentalTime::class)
internal class RealSlackSendSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackSearchRepository: SlackSearchRepository,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val clock: Clock,
) : SlackSendSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(dto: SlackSendSearchUseCase.Dto): Either<Throwable, SlackSentMessage> =
        withContext(dispatcher) {
            log.debug(tag, "Checking auth token before sending message: userId=${dto.userId}")
            val authState = SlackAuthState(
                searchSessionId = dto.searchSessionId,
                channelId = dto.channelId,
                teamId = dto.teamId,
                userId = dto.userId,
                responseUrl = dto.responseUrl,
                selfDestructSeconds = dto.selfDestructSeconds,
            )
            val token = slackRepository.getAuthToken(tokenId = dto.userId).getOrElse { error ->
                log.debug(tag, error) { "Error fetching user token${error.message?.let { ": $it" } ?: ""}" }
                return@withContext Either.Left(SlackSendSearchUseCase.Error.NotAuthenticated)
            }
            sendResult(authState = authState, authToken = token.token)
        }

    private suspend fun sendResult(
        authState: SlackAuthState,
        authToken: String,
    ): Either<Throwable, SlackSentMessage> {
        log.debug(tag, "Obtaining search session: searchSessionId=${authState.searchSessionId}")
        val searchSessionPost = slackSearchRepository.getSearchSessionPost(authState.searchSessionId)
            .getOrElse { return Either.Left(it) }
        log.debug(tag, "Cancelling previous search: responseUrl=${authState.responseUrl}")
        slackRepository.postMessageToUrl(
            url = authState.responseUrl,
            message = slackMessageFactory.cancelMessage(),
        ).getOrElse { return Either.Left(it) }
        log.debug(tag, "Posting search result: searchSessionId=${authState.searchSessionId}")
        val messageTs = slackRepository.postMessage(
            authToken = authToken,
            message = slackMessageFactory.searchPostMessage(
                searchQuery = searchSessionPost.searchQuery,
                attachmentTitle = searchSessionPost.attachmentTitle,
                attachmentUrl = searchSessionPost.attachmentUrl,
                attachmentImageUrl = searchSessionPost.attachmentImageUrl,
                channelId = authState.channelId,
                selfDestructSeconds = authState.selfDestructSeconds,
            ),
        ).getOrElse { return Either.Left(it) }
        val logPlaceholder = authState.selfDestructSeconds?.let { "self-destruct" } ?: "sent"
        val state = authState.selfDestructSeconds?.let {
            SlackSearchRepository.SearchSessionStateDto.SelfDestruct
        } ?: SlackSearchRepository.SearchSessionStateDto.Sent
        log.debug(tag, "Marking search session as $logPlaceholder: searchSessionId=${authState.searchSessionId}")
        slackSearchRepository.updateSearchSessionState(
            searchSessionId = authState.searchSessionId,
            state = state,
        ).getOrElse { return Either.Left(it) }
        val destroyTimestamp = authState.selfDestructSeconds?.let { seconds ->
            (clock.now() + seconds.seconds).toEpochMilliseconds()
        }
        val message = SlackSentMessage(
            id = authState.searchSessionId,
            userId = authState.userId,
            searchSessionId = authState.searchSessionId,
            destroyTimestamp = destroyTimestamp,
            channelId = authState.channelId,
            messageTs = messageTs,
        )
        if (message.isSelfDestruct) {
            log.debug(tag, "Persisting self-destruct state: searchSessionId=${authState.searchSessionId}")
            slackRepository.saveSelfDestructMessage(message).getOrElse { return Either.Left(it) }
        }
        return Either.Right(message)
    }
}