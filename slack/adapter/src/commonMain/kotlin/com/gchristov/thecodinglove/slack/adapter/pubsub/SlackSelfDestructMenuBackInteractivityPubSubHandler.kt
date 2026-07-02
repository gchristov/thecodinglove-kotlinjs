package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.DeserializationStrategy

internal class SlackSelfDestructMenuBackInteractivityPubSubHandler(
    private val slackSearchRepository: SlackSearchRepository,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val analytics: Analytics,
) : PubSubHandler<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> {
    override val dispatcher: CoroutineDispatcher get() = error("not used")
    override val jsonSerializer: JsonSerializer get() = error("not used")
    override val log: Logger get() = error("not used")
    override val pubSubDecoder: PubSubDecoder get() = error("not used")
    override val strategy: DeserializationStrategy<SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage> get() = error("not used")
    override fun httpConfig() = error("not used")

    override suspend fun handle(event: SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage): Either<Throwable, Unit> {
        val action = event.actions.firstOrNull { it.name == SlackActionName.SELF_DESTRUCT_MENU_BACK.apiValue }
            ?: return Either.Right(Unit)
        analytics.sendEvent(
            clientId = event.user.id,
            name = "slack_interactivity_self_destruct_menu_back",
            params = mapOf("user_id" to event.user.id, "team_id" to event.team.id),
        )
        val post = slackSearchRepository.getSearchSessionPost(action.value).getOrElse { return Either.Left(it) }
        return slackRepository.postMessageToUrl(
            url = event.responseUrl,
            message = slackMessageFactory.searchResultMessage(
                searchQuery = post.searchQuery,
                searchResults = post.searchResults,
                searchSessionId = action.value,
                attachmentTitle = post.attachmentTitle,
                attachmentUrl = post.attachmentUrl,
                attachmentImageUrl = post.attachmentImageUrl,
            ),
        )
    }
}
