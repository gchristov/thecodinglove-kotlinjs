package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.debug
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessageFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackShuffleSearchUseCase {
    suspend operator fun invoke(
        responseUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit>
}

internal class RealSlackShuffleSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchUseCase: SearchUseCase,
    private val slackRepository: SlackRepository,
) : SlackShuffleSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(
        responseUrl: String,
        searchSessionId: String,
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        log.debug(tag, "Shuffling search result: searchSessionId=$searchSessionId")
        searchUseCase.invoke(SearchUseCase.Type.WithSessionId(searchSessionId))
            .flatMap { searchResult ->
                log.debug(tag, "Sending search response: responseUrl=$responseUrl")
                slackRepository.postMessageToUrl(
                    url = responseUrl,
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