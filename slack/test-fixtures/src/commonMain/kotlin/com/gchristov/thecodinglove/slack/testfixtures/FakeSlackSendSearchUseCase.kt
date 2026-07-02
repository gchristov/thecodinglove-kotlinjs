package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import kotlin.test.assertEquals

class FakeSlackSendSearchUseCase(
    private val invocationResult: Either<Throwable, SlackSentMessage> = Either.Right(SlackSentMessageCreator.message()),
) : SlackSendSearchUseCase {
    private var invocations = 0
    private var lastSelfDestructMinutes: Int? = null

    override suspend fun invoke(dto: SlackSendSearchUseCase.Dto): Either<Throwable, SlackSentMessage> {
        lastSelfDestructMinutes = dto.selfDestructMinutes
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
    fun assertNotInvoked() = assertEquals(expected = 0, actual = invocations)
    fun assertSelfDestructMinutes(minutes: Int?) = assertEquals(expected = minutes, actual = lastSelfDestructMinutes)
}
