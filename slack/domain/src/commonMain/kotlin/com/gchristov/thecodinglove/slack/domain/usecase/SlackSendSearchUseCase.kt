package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

interface SlackSendSearchUseCase {
    /**
     * @return the [SlackSelfDestructMessage] to schedule for deletion if the sent message should
     * self-destruct, or `null` if it isn't self-destructing.
     */
    suspend operator fun invoke(dto: Dto): Either<Throwable, SlackSelfDestructMessage?>

    sealed class Error(message: String? = null) : Throwable(message) {
        data object NotAuthenticated : Error("User is not authenticated")
    }

    data class Dto(
        val userId: String,
        val teamId: String,
        val channelId: String,
        val responseUrl: String,
        val searchSessionId: String,
        val selfDestructMinutes: Int? = null,
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

    override suspend operator fun invoke(dto: SlackSendSearchUseCase.Dto): Either<Throwable, SlackSelfDestructMessage?> =
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
            val token = slackRepository.getAuthToken(tokenId = dto.userId).getOrElse { error ->
                log.debug(tag, error) { "Error fetching user token${error.message?.let { ": $it" } ?: ""}" }
                return@withContext Either.Left(SlackSendSearchUseCase.Error.NotAuthenticated)
            }
            sendResult(authState = authState, authToken = token.token)
        }

    private suspend fun sendResult(
        authState: SlackAuthState,
        authToken: String,
    ): Either<Throwable, SlackSelfDestructMessage?> {
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
                selfDestructMinutes = authState.selfDestructMinutes,
            ),
        ).getOrElse { return Either.Left(it) }
        val logPlaceholder = authState.selfDestructMinutes?.let { "self-destruct" } ?: "sent"
        val state = authState.selfDestructMinutes?.let {
            SlackSearchRepository.SearchSessionStateDto.SelfDestruct
        } ?: SlackSearchRepository.SearchSessionStateDto.Sent
        log.debug(tag, "Marking search session as $logPlaceholder: searchSessionId=${authState.searchSessionId}")
        slackSearchRepository.updateSearchSessionState(
            searchSessionId = authState.searchSessionId,
            state = state,
        ).getOrElse { return Either.Left(it) }
        val selfDestructMessage = authState.selfDestructMinutes?.let { minutes ->
            log.debug(tag, "Persisting self-destruct state: searchSessionId=${authState.searchSessionId}")
            val destroyTimestamp = clock.now().plus(value = minutes, unit = DateTimeUnit.MINUTE).toEpochMilliseconds()
            val message = SlackSelfDestructMessage(
                id = authState.searchSessionId,
                userId = authState.userId,
                searchSessionId = authState.searchSessionId,
                destroyTimestamp = destroyTimestamp,
                channelId = authState.channelId,
                messageTs = messageTs,
            )
            slackRepository.saveSelfDestructMessage(message).getOrElse { return Either.Left(it) }
            message
        }
        return Either.Right(selfDestructMessage)
    }
}