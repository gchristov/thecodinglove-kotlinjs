package com.gchristov.thecodinglove.slack.interactivity

import arrow.core.*
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.BaseHttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.sendEmpty
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.slackdata.api.ApiSlackInteractivity
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackInteractivityPubSubMessage
import com.gchristov.thecodinglove.slackdata.domain.toPubSubMessage
import com.gchristov.thecodinglove.slackdata.usecase.SlackVerifyRequestUseCase
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
    log = log
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
        slackVerifyRequestUseCase(request)
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
            strategy = SlackInteractivityPubSubMessage.serializer(),
        )
}