package com.gchristov.thecodinglove.slackdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import diglol.crypto.Hmac
import diglol.encoding.encodeHexToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.plus

interface SlackVerifyRequestUseCase {
    suspend operator fun invoke(request: ApiRequest): Either<Error, Unit>

    sealed class Error(message: String? = null) : Throwable(message) {
        object MissingTimestamp : Error()
        object MissingSignature : Error()
        object TooOld : Error()
        object SignatureMismatch : Error()
        data class Other(override val message: String?) : Error(message)
    }
}

class RealSlackVerifyRequestUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val slackConfig: SlackConfig,
    private val clock: Clock,
    private val log: Logger,
) : SlackVerifyRequestUseCase {
    override suspend fun invoke(request: ApiRequest): Either<SlackVerifyRequestUseCase.Error, Unit> =
        withContext(dispatcher) {
            try {
                val timestamp: Long = request.headers.get<String>("x-slack-request-timestamp")
                    ?.toLong()
                    ?: return@withContext Either.Left(SlackVerifyRequestUseCase.Error.MissingTimestamp)
                val signature: String = request.headers["x-slack-signature"]
                    ?: return@withContext Either.Left(SlackVerifyRequestUseCase.Error.MissingSignature)
                log.d("Verifying Slack request: timestamp=$timestamp, signature=$signature, body=${request.rawBody}")

                verifyTimestamp(
                    timestamp = timestamp,
                    validityMinutes = slackConfig.timestampValidityMinutes,
                    clock = clock
                ).flatMap {
                    verifyRequest(
                        timestamp = timestamp,
                        signature = signature,
                        rawBody = request.rawBody,
                        signingSecret = slackConfig.signingSecret
                    )
                }
            } catch (error: Throwable) {
                Either.Left(SlackVerifyRequestUseCase.Error.Other(
                    message = "Error during Slack request verification${error.message?.let { ": $it" } ?: ""}",
                ))
            }
        }

    private fun verifyTimestamp(
        timestamp: Long,
        validityMinutes: Int,
        clock: Clock
    ): Either<SlackVerifyRequestUseCase.Error, Unit> {
        val timestampInstant = Instant.fromEpochSeconds(timestamp).plus(
            value = validityMinutes,
            unit = DateTimeUnit.MINUTE
        )
        return if (timestampInstant < clock.now()) {
            Either.Left(SlackVerifyRequestUseCase.Error.TooOld)
        } else {
            Either.Right(Unit)
        }
    }

    private suspend fun verifyRequest(
        timestamp: Long,
        signature: String,
        rawBody: String?,
        signingSecret: String
    ): Either<SlackVerifyRequestUseCase.Error, Unit> {
        val baseString = "$Version:$timestamp:$rawBody"
        val data = baseString.encodeToByteArray()
        val key = signingSecret.encodeToByteArray()
        val cypher = Hmac(
            type = Hmac.Type.SHA256,
            key = key
        )
        val digest = cypher.compute(data)
        val computedSignature = "$Version=${digest.encodeHexToString()}"
        return if (computedSignature.compareTo(
                other = signature,
                ignoreCase = true
            ) == 0
        ) {
            Either.Right(Unit)
        } else {
            Either.Left(SlackVerifyRequestUseCase.Error.SignatureMismatch)
        }
    }
}

private val Version = "v0"