package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage

interface SlackRepository {
    suspend fun replyWithMessage(
        responseUrl: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>

    suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>
}

internal class RealSlackRepository(
    private val apiService: SlackApi,
    private val log: Logger,
) : SlackRepository {
    override suspend fun replyWithMessage(
        responseUrl: String,
        message: ApiSlackMessage
    ) = try {
        apiService.replyWithMessage(
            responseUrl = responseUrl,
            message = message
        )
        Either.Right(Unit)
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during message reply" }
        Either.Left(error)
    }

    override suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ) = try {
        apiService.postMessage(
            authToken = authToken,
            message = message
        )
        Either.Right(Unit)
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during message post" }
        Either.Left(error)
    }
}