package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.commonnetwork.NetworkClient
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SlackApi(private val client: NetworkClient.Json) {
    suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String,
    ): HttpResponse =
        client.http.get("https://slack.com/api/oauth.v2.access?code=$code&client_id=$clientId&client_secret=$clientSecret")

    suspend fun postMessageToUrl(
        url: String,
        message: ApiSlackMessage
    ): HttpResponse = client.http.post(url) {
        contentType(ContentType.Application.Json)
        setBody(message)
    }

    suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ): HttpResponse = client.http.post("https://slack.com/api/chat.postMessage") {
        bearerAuth(authToken)
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}