package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import kotlin.test.assertEquals
import kotlin.time.Duration

class FakeSlackSendSearchUseCase(
    private val invocationResult: Either<Throwable, SlackSentMessage> = Either.Right(SlackSentMessageCreator.message()),
) : SlackSendSearchUseCase {
    private var invocations = 0
    private var lastSelfDestructDelay: Duration? = null

    override suspend fun invoke(dto: SlackSendSearchUseCase.Dto): Either<Throwable, SlackSentMessage> {
        lastSelfDestructDelay = dto.selfDestructDelay
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
    fun assertNotInvoked() = assertEquals(expected = 0, actual = invocations)
    fun assertSelfDestructDelay(delay: Duration?) = assertEquals(expected = delay, actual = lastSelfDestructDelay)
}
