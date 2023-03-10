package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import com.gchristov.thecodinglove.slackdata.api.ApiSlackResponse
import io.ktor.client.call.*
import io.ktor.client.statement.*

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
        val slackResponse = apiService.replyWithMessage(
            responseUrl = responseUrl,
            message = message
        ).bodyAsText()
        if (slackResponse.lowercase() == "ok") {
            Either.Right(Unit)
        } else {
            throw Exception(slackResponse)
        }
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during message reply" }
        Either.Left(error)
    }

    override suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ) = try {
        val slackResponse: ApiSlackResponse = apiService.postMessage(
            authToken = authToken,
            message = message
        ).body()
        if (slackResponse.ok) {
            Either.Right(Unit)
        } else {
            throw Exception(slackResponse.error)
        }
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during message post" }
        Either.Left(error)
    }
}