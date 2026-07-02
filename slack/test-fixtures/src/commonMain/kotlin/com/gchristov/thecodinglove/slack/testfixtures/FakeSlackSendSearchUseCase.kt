package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import kotlin.test.assertEquals

class FakeSlackSendSearchUseCase(
    private val invocationResult: Either<Throwable, SlackSentMessage> = Either.Right(SlackSentMessageCreator.message()),
) : SlackSendSearchUseCase {
    private var invocations = 0
    private var lastSelfDestructSeconds: Long? = null

    override suspend fun invoke(dto: SlackSendSearchUseCase.Dto): Either<Throwable, SlackSentMessage> {
        lastSelfDestructSeconds = dto.selfDestructSeconds
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
    fun assertNotInvoked() = assertEquals(expected = 0, actual = invocations)
    fun assertSelfDestructSeconds(seconds: Long?) = assertEquals(expected = seconds, actual = lastSelfDestructSeconds)
}
