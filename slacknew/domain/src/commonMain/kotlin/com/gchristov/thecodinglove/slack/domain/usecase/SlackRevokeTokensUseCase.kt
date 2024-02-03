package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.ports.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackRevokeTokensUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Unit>

    data class Dto(
        val oAuth: List<String>?,
        val bot: List<String>?,
    )
}

internal class RealSlackRevokeTokensUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackRevokeTokensUseCase {
    private val tag = this::class.simpleName

    override suspend fun invoke(dto: SlackRevokeTokensUseCase.Dto): Either<Throwable, Unit> =
        withContext(dispatcher) {
            val tokensIdsToRevoke = (dto.oAuth?.toMutableList() ?: mutableListOf()).apply {
                addAll(dto.bot ?: emptyList())
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