package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase

class FakeSlackVerifyRequestUseCase(
    private val invocationResult: Either<SlackVerifyRequestUseCase.Error, Unit> = Either.Right(Unit),
) : SlackVerifyRequestUseCase {
    override suspend fun invoke(dto: SlackVerifyRequestUseCase.Dto) = invocationResult
}
