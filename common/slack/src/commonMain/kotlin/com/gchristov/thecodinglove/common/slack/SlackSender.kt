package com.gchristov.thecodinglove.common.slack

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.slack.api.SlackApi
import com.gchristov.thecodinglove.common.slack.api.mapper.toApiSlackMessage
import com.gchristov.thecodinglove.common.slack.api.mapper.toAuthToken
import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackAuthResponse
import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackDeleteMessage
import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackDeleteMessageResponse
import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackPostMessageResponse
import com.gchristov.thecodinglove.common.slack.api.model.ApiSlackReplyWithMessageResponse
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.slack.model.SlackMessage
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

class SlackSender(client: NetworkClient.Json) {
    private val api = SlackApi(client)

    suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String,
    ): Either<Throwable, SlackAuthToken> = try {
        val response: ApiSlackAuthResponse = api.authUser(
            code = code,
            clientId = clientId,
            clientSecret = clientSecret,
        ).body()
        if (response.ok) {
            Either.Right(response.toAuthToken())
        } else {
            throw Exception(response.error)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during user auth${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    suspend fun postMessageToUrl(
        url: String,
        message: SlackMessage,
    ): Either<Throwable, Unit> = try {
        val slackResponse = api.postMessageToUrl(
            url = url,
            message = message.toApiSlackMessage(),
        )
        // Sending requests to Slack response URLs currently has an issue where the content type
        // does not honor the Accept header, so we get text/plain instead of application/json
        if (slackResponse.contentType()?.match(ContentType.Application.Json) == true) {
            val jsonResponse: ApiSlackReplyWithMessageResponse = slackResponse.body()
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
            message = "Error during message reply${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    suspend fun postMessage(
        authToken: String,
        message: SlackMessage,
    ): Either<Throwable, String> = try {
        val response: ApiSlackPostMessageResponse = api.postMessage(
            authToken = authToken,
            message = message.toApiSlackMessage(),
        ).body()
        if (response.ok) {
            Either.Right(response.messageTs)
        } else {
            throw Exception(response.error)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during message post${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    suspend fun deleteMessage(
        authToken: String,
        channelId: String,
        messageTs: String,
    ): Either<Throwable, Unit> = try {
        val response: ApiSlackDeleteMessageResponse = api.deleteMessage(
            authToken = authToken,
            deleteMessage = ApiSlackDeleteMessage(
                channelId = channelId,
                messageTs = messageTs,
            ),
        ).body()
        when {
            response.ok -> Either.Right(Unit)
            // A message might have been deleted by the time self destruct attempts to delete it,
            // so just assume it's already gone if Slack tells us it doesn't exist.
            response.error == "message_not_found" -> Either.Right(Unit)
            // The app might not have permission to delete the message by the time self destruct
            // attempts to delete it, so just consume the error and leave the message there.
            response.error == "cant_delete_message" -> Either.Right(Unit)
            else -> throw Exception(response.error)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during message delete${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}
