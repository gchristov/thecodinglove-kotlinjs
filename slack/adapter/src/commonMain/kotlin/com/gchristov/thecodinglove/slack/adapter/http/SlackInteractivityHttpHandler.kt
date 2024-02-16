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
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackInteractivity
import com.gchristov.thecodinglove.slack.proto.pubsub.PubSubSlackInteractivityMessage
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackInteractivityHttpHandler(
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
        path = "/api/slack/interactivity",
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
                strategy = ApiSlackInteractivity.serializer(),
            )
        }
        .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
        .flatMap { publishInteractivityMessage(it) }
        .flatMap { response.sendEmpty() }

    private suspend fun publishInteractivityMessage(interactivity: ApiSlackInteractivity) = pubSubPublisher
        .publishJson(
            topic = slackConfig.interactivityPubSubTopic,
            body = interactivity.toPubSubMessage(),
            jsonSerializer = jsonSerializer,
            strategy = PubSubSlackInteractivityMessage.serializer(),
        )
}