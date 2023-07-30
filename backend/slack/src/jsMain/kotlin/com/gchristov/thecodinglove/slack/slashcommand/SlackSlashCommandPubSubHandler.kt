package com.gchristov.thecodinglove.slack.slashcommand

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubRequest
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSubSubscription
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubTopic
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json
import kotlin.io.encoding.ExperimentalEncodingApi

class SlackSlashCommandPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
    log: Logger,
    private val slackRepository: SlackRepository,
    private val searchUseCase: SearchUseCase,
    pubSubSubscription: PubSubSubscription,
) : BasePubSubHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
    pubSubSubscription = pubSubSubscription,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/slash",
        contentType = ContentType.Application.Json,
    )

    override fun pubSubConfig() = PubSubHandler.PubSubConfig(
        topic = SlackSlashCommandPubSubTopic,
    )

    @ExperimentalEncodingApi
    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = SlackSlashCommandPubSubMessage.serializer(),
        )
            .leftIfNull { Exception("PubSub request body missing") }
            .flatMap { it.handle() }

    private suspend fun SlackSlashCommandPubSubMessage.handle() = slackRepository.replyWithMessage(
        responseUrl = responseUrl,
        message = ApiSlackMessageFactory.processingMessage()
    )
        .flatMap { searchUseCase(SearchUseCase.Type.NewSession(query = text)) }
        .flatMap { searchResult ->
            slackRepository.replyWithMessage(
                responseUrl = responseUrl,
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