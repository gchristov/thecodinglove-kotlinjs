package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository

interface SlackReportExceptionUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Unit>

    data class Dto(
        val message: String,
        val stacktrace: String,
    )
}

internal class RealSlackReportExceptionUseCase(
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackConfig: SlackConfig,
) : SlackReportExceptionUseCase {
    override suspend fun invoke(dto: SlackReportExceptionUseCase.Dto): Either<Throwable, Unit> {
        val attachment = slackMessageFactory.attachment(
            text = dto.stacktrace,
            color = "#D00000",
        )
        val message = slackMessageFactory.message(
            text = dto.message,
            attachments = listOfNotNull(attachment),
        )
        return slackRepository.postMessageToUrl(
            url = slackConfig.monitoringUrl,
            message = message,
        )
    }
}