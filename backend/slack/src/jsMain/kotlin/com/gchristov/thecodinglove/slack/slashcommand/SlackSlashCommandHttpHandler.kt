package com.gchristov.thecodinglove.slack.slashcommand

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.BaseHttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.sendEmpty
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import com.gchristov.thecodinglove.slackdata.domain.toPubSubMessage
import com.gchristov.thecodinglove.slackdata.usecase.SlackVerifyRequestUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

class SlackSlashCommandHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
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
        path = "/api/slack/slash",
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
                strategy = ApiSlackSlashCommand.serializer(),
            )
        }
        .leftIfNull(default = { Exception("Request body is invalid") })
        .flatMap { publishSlashCommandMessage(it) }
        .flatMap { response.sendEmpty() }

    private suspend fun publishSlashCommandMessage(slashCommand: ApiSlackSlashCommand) = pubSubPublisher
        .publishJson(
            topic = slackConfig.slashCommandPubSubTopic,
            body = slashCommand.toPubSubMessage(),
            jsonSerializer = jsonSerializer,
            strategy = SlackSlashCommandPubSubMessage.serializer(),
        )
}