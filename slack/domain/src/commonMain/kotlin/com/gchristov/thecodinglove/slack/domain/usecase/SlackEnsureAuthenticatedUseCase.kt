package com.gchristov.thecodinglove.slack.domain.usecase

import arrow.core.Either
import arrow.core.getOrElse
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthState
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackEnsureAuthenticatedUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Result>

    sealed class Result {
        data object Authenticated : Result()
        data object AuthenticationPromptSent : Result()
    }

    data class Dto(
        val userId: String,
        val teamId: String,
        val channelId: String,
        val responseUrl: String,
        val searchSessionId: String,
        val selfDestructMinutes: Int? = null,
    )
}

internal class RealSlackEnsureAuthenticatedUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val log: Logger,
    private val slackRepository: SlackRepository,
    private val slackMessageFactory: SlackMessageFactory,
    private val slackConfig: SlackConfig,
) : SlackEnsureAuthenticatedUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(dto: SlackEnsureAuthenticatedUseCase.Dto): Either<Throwable, SlackEnsureAuthenticatedUseCase.Result> =
        withContext(dispatcher) {
            log.debug(tag, "Checking auth token: userId=${dto.userId}")
            val authState = SlackAuthState(
                searchSessionId = dto.searchSessionId,
                channelId = dto.channelId,
                teamId = dto.teamId,
                userId = dto.userId,
                responseUrl = dto.responseUrl,
                selfDestructMinutes = dto.selfDestructMinutes,
            )
            slackRepository.getAuthToken(tokenId = dto.userId).getOrElse { error ->
                log.debug(tag, error) { "Error fetching user token${error.message?.let { ": $it" } ?: ""}" }
                return@withContext sendAuthenticationPrompt(authState = authState)
            }
            Either.Right(SlackEnsureAuthenticatedUseCase.Result.Authenticated)
        }

    private suspend fun sendAuthenticationPrompt(
        authState: SlackAuthState,
    ): Either<Throwable, SlackEnsureAuthenticatedUseCase.Result> {
        log.debug(tag, "Asking user to authenticate: userId=${authState.userId}")
        return slackRepository.postMessageToUrl(
            url = authState.responseUrl,
            message = slackMessageFactory.authMessage(
                clientId = slackConfig.clientId,
                authState = authState,
            )
        ).map { SlackEnsureAuthenticatedUseCase.Result.AuthenticationPromptSent }
    }
}
