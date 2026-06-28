package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubDispatchHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubEventHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackInteractivityReceivedPubSubDispatchHandler internal constructor(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val eventHandlers: List<PubSubEventHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage>>,
    override val pubSubDecoder: PubSubDecoder,
) : PubSubDispatchHandler {
    private val tag = this::class.simpleName

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/interactivity-received",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        either {
            val body = request.decodeBodyFromJson(
                jsonSerializer = jsonSerializer,
                strategy = SlackInteractivityReceivedEvent.serializer(),
            ).bind() ?: raise(Exception("Request body is invalid"))
            val payload = body.payload as? SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage
                ?: raise(Exception("Unexpected payload type: ${body.payload::class.simpleName}"))
            eventHandlers.forEach { it.handle(payload).bind() }
        }.fold(
            ifLeft = { log.error(tag, it) { "Error handling request" }; Either.Right(Unit) },
            ifRight = { Either.Right(Unit) },
        )
}
