package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface ShuffleSlackSearchUseCase {
    suspend operator fun invoke(
        messageUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit>
}

class RealShuffleSlackSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchUseCase: SearchUseCase,
    private val slackRepository: SlackRepository,
) : ShuffleSlackSearchUseCase {
    override suspend operator fun invoke(
        messageUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.d("Shuffling search result: searchSessionId=$searchSessionId")
        searchUseCase.invoke(SearchUseCase.Type.WithSessionId(searchSessionId))
            .flatMap { searchResult ->
                log.d("Sending Slack response: messageUrl=$messageUrl")
                slackRepository.sendMessage(
                    messageUrl = messageUrl,
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