package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackRevokeTokensUseCase {
    suspend operator fun invoke(event: SlackEvent.Callback.Event.TokensRevoked): Either<Throwable, Unit>
}

internal class RealSlackRevokeTokensUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackRevokeTokensUseCase {
    private val tag = this::class.simpleName

    override suspend fun invoke(event: SlackEvent.Callback.Event.TokensRevoked): Either<Throwable, Unit> =
        withContext(dispatcher) {
            val tokensIdsToRevoke = (event.tokens.oAuth?.toMutableList() ?: mutableListOf()).apply {
                addAll(event.tokens.bot ?: emptyList())
            }
            log.debug(tag, "Processing revoked tokens: tokensIdsToRevoke=$tokensIdsToRevoke")
            tokensIdsToRevoke
                .map {
                    log.debug(tag, "Deleting token: id=$it")
                    slackRepository.deleteAuthToken(it)
                }
                .let { l -> either { l.bindAll() } }
                .map {}
        }
}