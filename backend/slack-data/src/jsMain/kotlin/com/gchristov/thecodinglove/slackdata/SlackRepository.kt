package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage

interface SlackRepository {
    suspend fun sendMessage(
        channelUrl: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>
}

internal class RealSlackRepository(
    private val apiService: SlackApi,
    private val log: Logger,
) : SlackRepository {
    override suspend fun sendMessage(
        channelUrl: String,
        message: ApiSlackMessage
    ) = try {
        apiService.sendMessage(
            messageUrl = channelUrl,
            message = message
        )
        Either.Right(Unit)
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during message send" }
        Either.Left(error)
    }
}