package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class SlackApi(private val client: HttpClient) {
    suspend fun sendMessage(
        messageUrl: String,
        message: ApiSlackMessage
    ): HttpResponse = client.post(messageUrl) {
        contentType(ContentType.Application.Json)
        setBody(message)
    }
}