package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.sequence
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackRevokeTokensUseCase {
    suspend operator fun invoke(event: SlackEvent.Callback.Event.TokensRevoked): Either<Throwable, Unit>
}

class RealSlackRevokeTokensUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackRevokeTokensUseCase {
    override suspend fun invoke(event: SlackEvent.Callback.Event.TokensRevoked): Either<Throwable, Unit> =
        withContext(dispatcher) {
            val tokensIdsToRevoke = (event.tokens.oAuth?.toMutableList() ?: mutableListOf()).apply {
                addAll(event.tokens.bot ?: emptyList())
            }
            log.d("Processing Slack revoked tokens: tokensIdsToRevoke=$tokensIdsToRevoke")
            tokensIdsToRevoke.map { slackRepository.deleteAuthToken(it) }.sequence().map {}
        }
}