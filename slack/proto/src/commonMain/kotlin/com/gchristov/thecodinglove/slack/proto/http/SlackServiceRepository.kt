package com.gchristov.thecodinglove.slack.proto.http

import arrow.core.Either
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackStatistics
import io.ktor.client.call.*

interface SlackServiceRepository {
    suspend fun slackStatistics(): Either<Throwable, ApiSlackStatistics>
}

internal class RealSlackServiceRepository(
    private val slackServiceApi: SlackServiceApi,
) : SlackServiceRepository {
    override suspend fun slackStatistics(): Either<Throwable, ApiSlackStatistics> = try {
        val response: ApiSlackStatistics = slackServiceApi.slackStatistics().body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during Slack statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}