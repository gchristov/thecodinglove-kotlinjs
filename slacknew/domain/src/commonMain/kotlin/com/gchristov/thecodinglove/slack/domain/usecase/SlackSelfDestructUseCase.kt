package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.ports.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

interface SlackSelfDestructUseCase {
    suspend operator fun invoke() : Either<Throwable, Unit>
}

internal class RealSlackSelfDestructUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val clock: Clock,
) : SlackSelfDestructUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(): Either<Throwable, Unit> = withContext(dispatcher) {
        log.debug(tag, "Fetching all self-destruct messages")
        slackRepository
            .getSelfDestructMessages()
            .flatMap { messages ->
                val messagesToDestroy = messages.filter { clock.now().toEpochMilliseconds() >= it.destroyTimestamp }
                log.debug(tag, "Self-destructing messages: total=${messages.size}, toDestroy=${messagesToDestroy.size}")
                messagesToDestroy
                    .map { message ->
                        log.debug(tag, "Obtaining user token: userId=${message.userId}")
                        slackRepository
                            .getAuthToken(tokenId = message.userId)
                            .fold(
                                ifLeft = {
                                    log.debug(tag, "User token not found, deleting self-destruct state: message=${message.id}")
                                    slackRepository.deleteSelfDestructMessage(message.id)
                                },
                                ifRight = { token ->
                                    log.debug(tag, "Deleting Slack message: message=${message.id}")
                                    slackRepository
                                        .deleteMessage(
                                            authToken = token.token,
                                            channelId = message.channelId,
                                            messageTs = message.messageTs,
                                        )
                                        .flatMap {
                                            log.debug(tag, "Deleting self-destruct state: message=${message.id}")
                                            slackRepository.deleteSelfDestructMessage(message.id)
                                        }
                                }
                            )
                    }
                    .let { l -> either { l.bindAll() } }
            }
    }
}