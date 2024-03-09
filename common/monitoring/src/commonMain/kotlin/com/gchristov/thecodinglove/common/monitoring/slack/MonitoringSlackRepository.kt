package com.gchristov.thecodinglove.common.monitoring.slack

import arrow.core.Either
import com.gchristov.thecodinglove.common.monitoring.slack.model.ApiMonitoringSlackMessage
import com.gchristov.thecodinglove.common.monitoring.slack.model.ApiMonitoringSlackPostMessageResponse
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal interface MonitoringSlackRepository {
    suspend fun reportException(
        message: String,
        stacktrace: String,
    ): Either<Throwable, Unit>
}

internal class RealMonitoringSlackRepository(
    private val monitoringSlackApi: MonitoringSlackApi,
) : MonitoringSlackRepository {
    override suspend fun reportException(
        message: String,
        stacktrace: String,
    ) = try {
        val monitoringSlackMessage = ApiMonitoringSlackMessage(
            text = message,
            attachments = listOf(ApiMonitoringSlackMessage.ApiAttachment(
                text = stacktrace,
                color = "#D00000",
            )),
        )
        val slackResponse = monitoringSlackApi.reportException(monitoringSlackMessage)
        // Sending requests to Slack response URLs currently has an issue where the content type
        // does not honor the Accept header, so we get text/plain instead of application/json
        if (slackResponse.contentType()?.match(ContentType.Application.Json) == true) {
            val jsonResponse: ApiMonitoringSlackPostMessageResponse = slackResponse.body()
            if (jsonResponse.ok) {
                Either.Right(Unit)
            } else {
                throw Exception(jsonResponse.error)
            }
        } else {
            val textResponse = slackResponse.bodyAsText()
            if (textResponse.lowercase() == "ok") {
                Either.Right(Unit)
            } else {
                throw Exception(textResponse)
            }
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during report exception${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}