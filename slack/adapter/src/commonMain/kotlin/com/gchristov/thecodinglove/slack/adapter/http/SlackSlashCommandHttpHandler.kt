package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toPubSubMessage
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toSlackRequestVerificationDto
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSlashCommandHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val pubSubPublisher: PubSubPublisher,
) : HttpHandler {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/slack/slash",
        contentType = ContentType.Application.FormUrlEncoded,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        if (slackConfig.requestVerificationEnabled) {
            val verifyDto = request.toSlackRequestVerificationDto().bind()
            slackVerifyRequestUseCase(verifyDto).bind()
        }
        val body = request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = ApiSlackSlashCommand.serializer(),
        ).bind() ?: raise(Exception("Request body is invalid"))
        publishSlashCommandMessage(body).bind()
        response.sendEmpty().bind()
    }

    private suspend fun publishSlashCommandMessage(slashCommand: ApiSlackSlashCommand) = pubSubPublisher
        .publishJson(
            topic = slackConfig.slashCommandReceivedPubSubTopic,
            body = slashCommand.toPubSubMessage(),
            jsonSerializer = jsonSerializer,
            strategy = SlackSlashCommandReceivedEvent.serializer(),
        )
}
