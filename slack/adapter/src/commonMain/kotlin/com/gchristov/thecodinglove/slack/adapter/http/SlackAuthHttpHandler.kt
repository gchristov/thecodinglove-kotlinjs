package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toAuthState
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackAuthState
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSelfDestructMessageEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.model.isSelfDestruct
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class SlackAuthHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val slackAuthUseCase: SlackAuthUseCase,
    private val slackSendSearchUseCase: SlackSendSearchUseCase,
    private val pubSubPublisher: PubSubPublisher,
    private val slackConfig: SlackConfig,
    private val analytics: Analytics,
) : HttpHandler {
    private val tag = this::class.simpleName

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/slack/auth",
        contentType = ContentType.Application.FormUrlEncoded,
    )

    @OptIn(ExperimentalTime::class)
    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        val code: String? = request.query["code"]
        val state = request.query.get<String?>("state").takeIf { !it.isNullOrEmpty() }
        return either {
            slackAuthUseCase(SlackAuthUseCase.Dto(code)).bind()
            // If there's state, this means we may be able to send (and possibly self-destruct) a message.
            state?.let {
                val sentMessage = handleAuthState(it).bind()
                if (sentMessage.isSelfDestruct) {
                    pubSubPublisher.publishJson(
                        topic = slackConfig.selfDestructMessagePubSubTopic,
                        body = SlackSelfDestructMessageEvent(
                            id = sentMessage.id,
                            userId = sentMessage.userId,
                            channelId = sentMessage.channelId,
                            messageTs = sentMessage.messageTs,
                        ),
                        jsonSerializer = jsonSerializer,
                        strategy = SlackSelfDestructMessageEvent.serializer(),
                        delay = Instant.fromEpochMilliseconds(requireNotNull(sentMessage.destroyTimestamp)) - Clock.System.now(),
                    ).bind()
                }
            }
            analytics.sendEvent(
                clientId = uuid4().toString(),
                name = "slack_auth_success",
            )
            response.redirect("/slack/auth/success").bind()
        }
    }

    override suspend fun handleError(
        error: Throwable,
        response: HttpResponse,
    ): Either<Throwable, Unit> = when (error) {
        is SlackAuthUseCase.Error.Cancelled -> {
            analytics.sendEvent(
                clientId = uuid4().toString(),
                name = "slack_auth_cancel",
            )
            response.redirect("/")
        }

        is SlackAuthUseCase.Error.Other -> {
            analytics.sendEvent(
                clientId = uuid4().toString(),
                name = "slack_auth_error",
                params = error.message?.let { mapOf("info" to it) }
            )
            response.redirect("/slack/auth/error")
        }

        else -> super<HttpHandler>.handleError(error, response)
    }

    private suspend fun handleAuthState(state: String) = try {
        val base64Decoded = state.decodeBase64String()
        log.debug(tag, "Decoded Base64 auth state: decoded=$base64Decoded")
        val authState = jsonSerializer.json
            .decodeFromString<ApiSlackAuthState>(base64Decoded)
            .toAuthState()
        log.debug(tag, "Parsed Base64 auth state: parsed=$authState")
        slackSendSearchUseCase.invoke(
            SlackSendSearchUseCase.Dto(
                userId = authState.userId,
                teamId = authState.teamId,
                channelId = authState.channelId,
                responseUrl = authState.responseUrl,
                searchSessionId = authState.searchSessionId,
                selfDestructDelay = authState.selfDestructDelay,
            )
        )
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error handling auth state${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}
