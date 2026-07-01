package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.HttpHandler
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SelfDestructSlackMessageEvent
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSelfDestructMessagePubSubHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    override val pubSubDecoder: PubSubDecoder,
    private val selfDestructUseCase: SlackSelfDestructUseCase,
) : PubSubHandler<SelfDestructSlackMessageEvent> {
    override val strategy = SelfDestructSlackMessageEvent.serializer()

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/self-destruct/message",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handle(event: SelfDestructSlackMessageEvent): Either<Throwable, Unit> = selfDestructUseCase(
        messageId = event.id,
        userId = event.userId,
        channelId = event.channelId,
        messageTs = event.messageTs,
    )
}
