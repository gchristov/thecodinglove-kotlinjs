package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackSelfDestructUseCase {
    suspend operator fun invoke(
        messageId: String,
        userId: String,
        channelId: String,
        messageTs: String,
    ): Either<Throwable, Unit>
}

internal class RealSlackSelfDestructUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackSelfDestructUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(
        messageId: String,
        userId: String,
        channelId: String,
        messageTs: String,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.debug(tag, "Obtaining user token: userId=$userId")
        val token = slackRepository.getAuthToken(tokenId = userId).getOrElse {
            log.debug(tag, "User token not found, deleting self-destruct state: message=$messageId")
            return@withContext slackRepository.deleteSelfDestructMessage(messageId)
        }
        log.debug(tag, "Deleting Slack message: message=$messageId")
        slackRepository.deleteMessage(
            authToken = token.token,
            channelId = channelId,
            messageTs = messageTs,
        ).getOrElse { return@withContext Either.Left(it) }
        log.debug(tag, "Deleting self-destruct state: message=$messageId")
        slackRepository.deleteSelfDestructMessage(messageId)
    }
}