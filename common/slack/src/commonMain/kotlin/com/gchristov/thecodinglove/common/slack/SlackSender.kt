package com.gchristov.thecodinglove.common.slack

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.network.safeApiCall
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
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.*
import io.ktor.http.*

class SlackSender(client: NetworkClient.Json) {
    private val api = SlackApi(client)

    suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String,
    ) = safeApiCall("Error during user auth") {
        val response: ApiSlackAuthResponse = api.authUser(
            code = code,
            clientId = clientId,
            clientSecret = clientSecret,
        ).body()
        if (response.ok) response.toAuthToken() else throw Exception(response.error)
    }

    suspend fun postMessageToUrl(
        url: String,
        message: SlackMessage,
    ) = safeApiCall("Error during message reply") {
        val slackResponse = try {
            api.postMessageToUrl(
                url = url,
                message = message.toApiSlackMessage(),
            )
        } catch (error: ClientRequestException) {
            // PubSub can retry a handler after an earlier transient failure elsewhere in the same
            // flow, replaying this call - but a response_url can only be used a limited number of
            // times. "used_url" means an earlier attempt already sent the message, so treat this as
            // success instead of failing (and triggering yet another retry).
            if (error.response.status == HttpStatusCode.NotFound && error.response.bodyAsText().trim() == "used_url") {
                return@safeApiCall Unit
            }
            throw error
        }
        // Sending requests to Slack response URLs currently has an issue where the content type
        // does not honor the Accept header, so we get text/plain instead of application/json
        if (slackResponse.contentType()?.match(ContentType.Application.Json) == true) {
            val jsonResponse: ApiSlackReplyWithMessageResponse = slackResponse.body()
            if (!jsonResponse.ok) throw Exception(jsonResponse.error)
        } else {
            val textResponse = slackResponse.bodyAsText()
            if (textResponse.lowercase() != "ok") throw Exception(textResponse)
        }
        Unit
    }

    suspend fun postMessage(
        authToken: String,
        message: SlackMessage,
    ) = safeApiCall("Error during message post") {
        val response: ApiSlackPostMessageResponse = api.postMessage(
            authToken = authToken,
            message = message.toApiSlackMessage(),
        ).body()
        if (response.ok) response.messageTs else throw Exception(response.error)
    }

    suspend fun deleteMessage(
        authToken: String,
        channelId: String,
        messageTs: String,
    ) = safeApiCall("Error during message delete") {
        val response: ApiSlackDeleteMessageResponse = api.deleteMessage(
            authToken = authToken,
            deleteMessage = ApiSlackDeleteMessage(
                channelId = channelId,
                messageTs = messageTs,
            ),
        ).body()
        when {
            response.ok -> Unit
            // A message might have been deleted by the time self destruct attempts to delete it,
            // so just assume it's already gone if Slack tells us it doesn't exist.
            response.error == "message_not_found" -> Unit
            // The app might not have permission to delete the message by the time self destruct
            // attempts to delete it, so just consume the error and leave the message there.
            response.error == "cant_delete_message" -> Unit
            else -> throw Exception(response.error)
        }
    }
}
