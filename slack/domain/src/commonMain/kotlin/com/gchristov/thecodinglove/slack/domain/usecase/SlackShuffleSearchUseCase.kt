package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
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
    private val slackSearchRepository: SlackSearchRepository,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
) : SlackShuffleSearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(dto: SlackShuffleSearchUseCase.Dto): Either<Throwable, Unit> =
        withContext(dispatcher) {
            log.debug(tag, "Shuffling search result: searchSessionId=${dto.searchSessionId}")
            val shuffleResult = slackSearchRepository.shuffle(dto.searchSessionId)
                .getOrElse { return@withContext Either.Left(it) }
            if (!shuffleResult.ok || shuffleResult.searchSession == null) {
                // Swallow but report the error, so that we can investigate. At this point, the user will be seeing
                // a post with interactivity options, so from their POV nothing will happen, so they can re-shuffle.
                log.error(tag, Throwable(shuffleResult.error?.let {
                    when (it) {
                        is SlackSearchRepository.SearchResultDto.Error.NoResults -> "No results found"
                    }
                } ?: "Unknown error")) { "Error shuffling" }
                return@withContext Either.Right(Unit)
            }
            log.debug(tag, "Sending shuffle response: responseUrl=${dto.responseUrl}")
            slackRepository.postMessageToUrl(
                url = dto.responseUrl,
                message = slackMessageFactory.searchResultMessage(
                    searchQuery = shuffleResult.searchSession.post.searchQuery,
                    searchResults = shuffleResult.searchSession.searchResults,
                    searchSessionId = shuffleResult.searchSession.searchSessionId,
                    attachmentTitle = shuffleResult.searchSession.post.attachmentTitle,
                    attachmentUrl = shuffleResult.searchSession.post.attachmentUrl,
                    attachmentImageUrl = shuffleResult.searchSession.post.attachmentImageUrl,
                )
            )
        }
}