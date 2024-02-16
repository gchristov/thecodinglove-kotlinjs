package com.gchristov.thecodinglove.slack.proto.http

import arrow.core.Either
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackStatistics
import io.ktor.client.call.*

interface SlackServiceRepository {
    suspend fun slackStatistics(): Either<Throwable, ApiSlackStatistics>

    suspend fun selfDestruct(): Either<Throwable, Unit>
}

internal class RealSlackServiceRepository(
    private val slackServiceApi: SlackServiceApi,
) : SlackServiceRepository {
    override suspend fun slackStatistics() = try {
        val response: ApiSlackStatistics = slackServiceApi.slackStatistics().body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun selfDestruct() = try {
        slackServiceApi.selfDestruct()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during self-destruct${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}