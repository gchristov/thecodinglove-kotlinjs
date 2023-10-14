package com.gchristov.thecodinglove.slack.slashcommand

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.leftIfNull
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.pubsub.BasePubSubHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubHandler
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.domain.SlackSlashCommandPubSubMessage
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.serialization.json.Json

class SlackSlashCommandPubSubHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val searchUseCase: SearchUseCase,
    pubSubSubscription: PubSubSubscription,
    pubSubDecoder: PubSubDecoder,
    private val slackConfig: SlackConfig,
) : BasePubSubHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
    pubSubSubscription = pubSubSubscription,
    pubSubDecoder = pubSubDecoder,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/pubsub/slack/slash",
        contentType = ContentType.Application.Json,
    )

    override fun pubSubConfig() = PubSubHandler.PubSubConfig(
        topic = slackConfig.slashCommandPubSubTopic,
    )

    override suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit> =
        request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = SlackSlashCommandPubSubMessage.serializer(),
        )
            .leftIfNull { Exception("PubSub request body missing") }
            .flatMap { it.handle() }
            .fold(
                ifLeft = {
                    when {
                        // If the Slack response url has expired we can't really do much else, so we ACK it
                        it.message?.contains("used_url") == true -> {
                            log.w(it) { "Ignoring PubSub error for expired Slack url" }
                            Either.Right(Unit)
                        }

                        else -> Either.Left(it)
                    }
                }, ifRight = { Either.Right(Unit) }
            )

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