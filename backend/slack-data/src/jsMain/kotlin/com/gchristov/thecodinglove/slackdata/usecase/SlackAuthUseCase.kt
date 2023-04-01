package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackAuthUseCase {
    suspend operator fun invoke(
        code: String?,
        searchSessionId: String?,
    ): Either<Error, Unit>

    sealed class Error(message: String? = null) : Throwable(message) {
        object Cancelled : Error()
        data class Other(override val message: String?) : Error(message)
    }
}

class RealSlackAuthUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val slackConfig: SlackConfig,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackAuthUseCase {
    override suspend fun invoke(
        code: String?,
        searchSessionId: String?
    ): Either<SlackAuthUseCase.Error, Unit> = withContext(dispatcher) {
        log.d("Processing Slack user auth request: code=$code, searchSessionId=$searchSessionId")
        if (code.isNullOrEmpty()) {
            log.d("Auth cancelled")
            Either.Left(SlackAuthUseCase.Error.Cancelled)
        } else {
            slackRepository.authUser(
                code = code,
                clientId = slackConfig.clientId,
                clientSecret = slackConfig.clientSecret
            )
                .mapLeft { SlackAuthUseCase.Error.Other(it.message) }
                .flatMap { authResponse ->
                    log.d("Persisting user token")
                    slackRepository
                        .saveAuthToken(authResponse)
                        .mapLeft { SlackAuthUseCase.Error.Other(it.message) }
                }
        }
    }
}