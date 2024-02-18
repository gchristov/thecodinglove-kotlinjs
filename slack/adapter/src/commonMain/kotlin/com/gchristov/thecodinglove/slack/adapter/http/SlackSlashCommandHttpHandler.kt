package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toPubSubMessage
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toSlackRequestVerificationDto
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.PubSubSlackSlashCommandMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSlashCommandHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val pubSubPublisher: PubSubPublisher,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/slack/slash",
        contentType = ContentType.Application.FormUrlEncoded,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = if (slackConfig.requestVerificationEnabled) {
        request
            .toSlackRequestVerificationDto()
            .flatMap { slackVerifyRequestUseCase(it) }
    } else {
        Either.Right(Unit)
    }
        .flatMap {
            request.decodeBodyFromJson(
                jsonSerializer = jsonSerializer,
                strategy = ApiSlackSlashCommand.serializer(),
            )
        }
        .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
        .flatMap { publishSlashCommandMessage(it) }
        .flatMap { response.sendEmpty() }

    private suspend fun publishSlashCommandMessage(slashCommand: ApiSlackSlashCommand) = pubSubPublisher
        .publishJson(
            topic = slackConfig.slashCommandPubSubTopic,
            body = slashCommand.toPubSubMessage(),
            jsonSerializer = jsonSerializer,
            strategy = PubSubSlackSlashCommandMessage.serializer(),
        )
}