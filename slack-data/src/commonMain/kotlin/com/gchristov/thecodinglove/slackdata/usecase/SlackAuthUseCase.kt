package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.debug
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SlackAuthUseCase {
    suspend operator fun invoke(code: String?): Either<Error, Unit>

    sealed class Error(override val message: String? = null) : Throwable(message) {
        object Cancelled : Error()
        data class Other(
            val additionalInfo: String? = null
        ) : Error("Auth error${additionalInfo?.let { ": $it" } ?: ""}")
    }
}

internal class RealSlackAuthUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val slackConfig: SlackConfig,
    private val log: Logger,
    private val slackRepository: SlackRepository,
) : SlackAuthUseCase {
    private val tag = this::class.simpleName

    override suspend fun invoke(code: String?): Either<SlackAuthUseCase.Error, Unit> = withContext(dispatcher) {
        log.debug(tag, "Processing user auth request: code=$code")
        if (code.isNullOrEmpty()) {
            log.debug(tag, "Auth cancelled")
            Either.Left(SlackAuthUseCase.Error.Cancelled)
        } else {
            slackRepository.authUser(
                code = code,
                clientId = slackConfig.clientId,
                clientSecret = slackConfig.clientSecret
            )
                .mapLeft { SlackAuthUseCase.Error.Other(it.message) }
                .flatMap { authResponse ->
                    log.debug(tag, "Persisting auth token")
                    slackRepository
                        .saveAuthToken(authResponse)
                        .mapLeft { SlackAuthUseCase.Error.Other(it.message) }
                }
        }
    }
}