package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.model.SlackStatistics
import com.gchristov.thecodinglove.slack.domain.usecase.SlackStatisticsUseCase
import kotlin.test.assertEquals

class FakeSlackStatisticsUseCase(
    private val invocationResult: Either<Throwable, SlackStatistics> = Either.Right(SlackStatisticsCreator.statistics()),
) : SlackStatisticsUseCase {
    private var invocations = 0

    override suspend fun invoke(): Either<Throwable, SlackStatistics> {
        invocations++
        return invocationResult
    }

    fun assertInvokedOnce() = assertEquals(expected = 1, actual = invocations)
}

object SlackStatisticsCreator {
    fun statistics(
        activeSelfDestructMessages: Int = 2,
        users: Int = 10,
        teams: Int = 5,
    ) = SlackStatistics(
        activeSelfDestructMessages = activeSelfDestructMessages,
        users = users,
        teams = teams,
    )
}
