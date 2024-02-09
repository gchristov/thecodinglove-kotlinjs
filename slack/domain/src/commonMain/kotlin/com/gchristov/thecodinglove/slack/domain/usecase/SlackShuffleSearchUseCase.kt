package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SearchEngine
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackShuffleSearchUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Unit>

    data class Dto(
        val responseUrl: String,
        val searchSessionId: String,
    )
}

internal class RealSlackShuffleSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val searchSessionShuffle: SearchEngine,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
) : SlackShuffleSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(dto: SlackShuffleSearchUseCase.Dto): Either<Throwable, Unit> =
        withContext(dispatcher) {
            log.debug(tag, "Shuffling search result: searchSessionId=${dto.searchSessionId}")
            searchSessionShuffle.shuffle(dto.searchSessionId)
                .flatMap { shuffleResult ->
                    log.debug(tag, "Sending shuffle response: responseUrl=${dto.responseUrl}")
                    slackRepository.postMessageToUrl(
                        url = dto.responseUrl,
                        message = slackMessageFactory.searchResultMessage(
                            searchQuery = shuffleResult.searchQuery,
                            searchResults = shuffleResult.searchResults,
                            searchSessionId = dto.searchSessionId,
                            attachmentTitle = shuffleResult.attachmentTitle,
                            attachmentUrl = shuffleResult.attachmentUrl,
                            attachmentImageUrl = shuffleResult.attachmentImageUrl,
                        )
                    )
                }
        }
}