package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toPubSubMessage
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toSlackRequestVerificationDto
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackInteractivity
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackInteractivityHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
    private val slackConfig: SlackConfig,
    private val pubSubPublisher: PubSubPublisher,
) : HttpHandler {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/slack/interactivity",
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
            strategy = ApiSlackInteractivity.serializer(),
        ).bind() ?: raise(Exception("Request body is invalid"))
        publishInteractivityMessage(body).bind()
        response.sendEmpty().bind()
    }

    private suspend fun publishInteractivityMessage(interactivity: ApiSlackInteractivity) = pubSubPublisher
        .publishJson(
            topic = slackConfig.interactivityReceivedPubSubTopic,
            body = interactivity.toPubSubMessage(),
            jsonSerializer = jsonSerializer,
            strategy = SlackInteractivityReceivedEvent.serializer(),
        )
}
