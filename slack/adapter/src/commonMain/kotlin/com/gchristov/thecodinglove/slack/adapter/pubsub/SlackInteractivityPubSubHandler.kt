package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.network.http.HttpResponse
import com.gchristov.thecodinglove.common.network.http.sendEmpty
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackInteractivityPubSubHandler internal constructor(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val eventHandlers: List<PubSubHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage>>,
    override val pubSubDecoder: PubSubDecoder,
) : PubSubHandler<SlackInteractivityReceivedEvent> {
    private val tag = this::class.simpleName

    override val strategy = SlackInteractivityReceivedEvent.serializer()

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/interactivity",
        contentType = ContentType.Application.Json,
    )

    // Swallow all errors — interactivity failures and parse errors should not trigger PubSub retries.
    override suspend fun handleError(error: Throwable, response: HttpResponse): Either<Throwable, Unit> {
        log.error(tag, error) { "Error handling request" }
        return response.sendEmpty()
    }

    override suspend fun handle(event: SlackInteractivityReceivedEvent): Either<Throwable, Unit> {
        either {
            val payload = event.payload as? SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage
                ?: raise(Exception("Unexpected payload type: ${event.payload::class.simpleName}"))
            eventHandlers.forEach { it.handle(payload).bind() }
        }.getOrElse {
            log.error(tag, it) { "Error handling request" }
            return Either.Right(Unit)
        }
        return Either.Right(Unit)
    }
}
