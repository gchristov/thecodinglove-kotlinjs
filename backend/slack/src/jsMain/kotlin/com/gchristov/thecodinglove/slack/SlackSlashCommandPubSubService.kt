package com.gchristov.thecodinglove.slack

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import com.gchristov.thecodinglove.commonservice.PubSubService
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.*
import com.gchristov.thecodinglove.search.PreloadPubSubService
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import kotlinx.serialization.json.Json

class SlackSlashCommandPubSubService(
    pubSubServiceRegister: PubSubServiceRegister,
    private val jsonSerializer: Json,
    private val slackRepository: SlackRepository,
    private val pubSubSender: PubSubSender,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
) : PubSubService(pubSubServiceRegister = pubSubServiceRegister) {
    override fun topic(): String = Topic

    override fun register() {
        exports.slackSlashCommandPubSub = registerForPubSubCallbacks()
    }

    override suspend fun handleMessage(message: PubSubMessage): Either<Throwable, Unit> =
        message.bodyAsJson<SlackSlashCommandPubSubMessage>(jsonSerializer)
            .leftIfNull(default = { Exception("Message body is null") })
            .flatMap { slashCommand ->
                slackRepository.sendMessage(
                    channelUrl = slashCommand.responseUrl,
                    message = ApiSlackMessageFactory.processingMessage()
                ).flatMap {
                    searchWithSessionUseCase(
                        SearchWithSessionUseCase.Type.NewSession(query = slashCommand.text)
                    ).flatMap { searchResult ->
                        publishPreloadMessage(searchResult.searchSessionId)
                            .flatMap {
                                slackRepository.sendMessage(
                                    channelUrl = slashCommand.responseUrl,
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

    private suspend fun publishPreloadMessage(searchSessionId: String) = pubSubSender.sendMessage(
        topic = PreloadPubSubService.Topic,
        body = PreloadPubSubMessage(searchSessionId),
        jsonSerializer = jsonSerializer
    )

    companion object {
        const val Topic = "slackSlashCommandPubSub"
    }
}