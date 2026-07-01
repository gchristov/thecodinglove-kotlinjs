package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSelfDestructUseCase
import kotlin.test.assertEquals

class FakeSlackSelfDestructUseCase(
    private val invocationResult: Either<Throwable, Unit> = Either.Right(Unit),
) : SlackSelfDestructUseCase {
    private var invocations = 0
    private var lastMessageId: String? = null

    override suspend fun invoke(
        messageId: String,
        userId: String,
        channelId: String,
        messageTs: String,
    ): Either<Throwable, Unit> {
        invocations++
        lastMessageId = messageId
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
    fun assertInvokedWith(messageId: String) = assertEquals(expected = messageId, actual = lastMessageId)
}
