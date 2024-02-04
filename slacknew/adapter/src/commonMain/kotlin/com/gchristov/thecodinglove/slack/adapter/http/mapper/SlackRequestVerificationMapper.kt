package com.gchristov.thecodinglove.slack.adapter.http.mapper

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase

internal fun HttpRequest.toSlackRequestVerificationDto() = try {
    val timestamp = headers.get<String>("x-slack-request-timestamp")?.toLong()
    val signature = headers.get<String>("x-slack-signature")
    Either.Right(
        SlackVerifyRequestUseCase.Dto(
            timestamp = requireNotNull(timestamp),
            signature = requireNotNull(signature),
            rawBody = bodyString,
        )
    )
} catch (error: Throwable) {
    Either.Left(Throwable(error.message))
}