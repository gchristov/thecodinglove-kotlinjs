package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SlackApi(private val client: HttpClient) {
    suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String,
    ): HttpResponse =
        client.get("https://slack.com/api/oauth.v2.access?code=$code&client_id=$clientId&client_secret=$clientSecret") {
            accept(ContentType.Application.Json)
        }

    suspend fun replyWithMessage(
        responseUrl: String,
        message: ApiSlackMessage
    ): HttpResponse = client.post(responseUrl) {
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        setBody(message)
    }

    suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ): HttpResponse = client.post("https://slack.com/api/chat.postMessage") {
        bearerAuth(authToken)
        contentType(ContentType.Application.Json)
        accept(ContentType.Application.Json)
        setBody(message)
    }
}