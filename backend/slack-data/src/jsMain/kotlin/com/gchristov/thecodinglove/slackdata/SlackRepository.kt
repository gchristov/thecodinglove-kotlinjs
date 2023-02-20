package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.sendJson
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import kotlinx.serialization.json.Json

interface SlackRepository {
    fun sendProcessingMessage(
        text: String,
        response: ApiResponse
    ): Either<Throwable, Unit>

    suspend fun sendMessage(
        messageUrl: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>
}

internal class RealSlackRepository(
    private val apiService: SlackApi,
    private val jsonSerializer: Json
) : SlackRepository {
    override fun sendProcessingMessage(
        text: String,
        response: ApiResponse
    ) = response.sendJson(
        data = ApiSlackMessage.ApiProcessing(text = text),
        jsonSerializer = jsonSerializer
    )

    override suspend fun sendMessage(
        messageUrl: String,
        message: ApiSlackMessage
    ) = try {
        apiService.sendMessage(
            messageUrl = messageUrl,
            message = message
        )
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(error)
    }
}