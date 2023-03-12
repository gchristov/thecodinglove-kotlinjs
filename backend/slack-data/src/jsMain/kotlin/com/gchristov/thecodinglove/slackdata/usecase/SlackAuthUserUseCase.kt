package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackAuthUserUseCase {
    suspend operator fun invoke(
        code: String?,
        searchSessionId: String?,
    ): Either<Error, Unit>

    sealed class Error(message: String? = null) : Throwable(message) {
        object Cancelled : Error()
        data class Other(override val message: String?) : Error(message)
    }
}

class RealSlackAuthUserUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val slackConfig: SlackConfig,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackAuthUserUseCase {
    override suspend fun invoke(
        code: String?,
        searchSessionId: String?
    ): Either<SlackAuthUserUseCase.Error, Unit> = withContext(dispatcher) {
        log.d("Processing Slack user auth request: code=$code, searchSessionId=$searchSessionId")
        if (code.isNullOrEmpty()) {
            log.d("Auth cancelled")
            Either.Left(SlackAuthUserUseCase.Error.Cancelled)
        } else {
            slackRepository.authUser(
                code = code,
                clientId = slackConfig.clientId,
                clientSecret = slackConfig.signingSecret
            )
                .mapLeft { SlackAuthUserUseCase.Error.Other(it.message) }
                .flatMap { authResponse ->
                    log.d("Persisting user token")
                    // TODO: Persist user token
                    println(authResponse)
                    Either.Right(Unit)
                }
        }
    }
}