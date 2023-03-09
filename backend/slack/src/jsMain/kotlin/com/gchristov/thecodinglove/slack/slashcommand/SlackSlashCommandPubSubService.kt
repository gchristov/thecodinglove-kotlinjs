package com.gchristov.thecodinglove.slack.slashcommand

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.decodeBodyFromJson
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubTopic
import kotlinx.serialization.json.Json

class SlackSlashCommandPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val searchUseCase: SearchUseCase,
) : PubSubService(
    pubSubServiceRegister = pubSubServiceRegister,
    log = log,
) {
    override fun topic(): String = SlackSlashCommandPubSubTopic

    override fun register() {
        exports.slackSlashCommandPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> =
        message.decodeBodyFromJson<SlackSlashCommandPubSubMessage>(
            jsonSerializer = jsonSerializer,
            log = log
        )
            .leftIfNull(default = { Exception("Message body is null") })
            .flatMap { slashCommand ->
                slackRepository.sendMessage(
                    messageUrl = slashCommand.responseUrl,
                    message = ApiSlackMessageFactory.processingMessage()
                ).flatMap {
                    searchUseCase(
                        SearchUseCase.Type.NewSession(query = slashCommand.text)
                    ).flatMap { searchResult ->
                        slackRepository.sendMessage(
                            messageUrl = slashCommand.responseUrl,
                            message = ApiSlackMessageFactory.searchResultMessage(
                                searchQuery = searchResult.query,
                                searchResults = searchResult.totalPosts,
                                searchSessionId = searchResult.searchSessionId,
                                attachmentTitle = searchResult.post.title,
                                attachmentUrl = searchResult.post.url,
                                attachmentImageUrl = searchResult.post.imageUrl
                            )
                        )
                    }
                }
            }
}