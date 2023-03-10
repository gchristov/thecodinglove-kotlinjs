package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SlackApi(private val client: HttpClient) {
    suspend fun replyWithMessage(
        responseUrl: String,
        message: ApiSlackMessage
    ): HttpResponse = client.post(responseUrl) {
        contentType(ContentType.Application.Json)
        setBody(message)
    }

    suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ): HttpResponse = client.post("https://slack.com/api/chat.postMessage") {
        headers {
            set(name = "Authorization", value = "Bearer $authToken")
        }
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}