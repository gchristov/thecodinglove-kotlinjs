package com.gchristov.thecodinglove.slack.usecase

import arrow.core.Either
import arrow.core.flatMap
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

internal class RealVerifySlackRequestUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val slackConfig: SlackConfig,
) : VerifySlackRequestUseCase {
    override suspend fun invoke(request: ApiRequest): Either<Throwable, Unit> =
        withContext(dispatcher) {
            try {
                val timestamp: Long = request.headers.get<String>("x-slack-request-timestamp")
                    ?.toLong()
                    ?: return@withContext Either.Left(Throwable(GenericError))
                val signature: String = request.headers["x-slack-signature"]
                    ?: return@withContext Either.Left(Throwable(GenericError))
                println("Verifying Slack request\n" +
                        "timestamp: $timestamp\n" +
                        "signature: $signature\n" +
                        "body: ${request.rawBody}")

                verifyTimestamp(
                    timestamp = timestamp,
                    validityMinutes = slackConfig.timestampValidityMinutes
                ).flatMap {
                    verifyRequest(
                        timestamp = timestamp,
                        signature = signature,
                        rawBody = request.rawBody,
                        signingSecret = slackConfig.signingSecret
                    )
                }
            } catch (error: Throwable) {
                Either.Left(error)
            }
        }

    private fun verifyTimestamp(
        timestamp: Long,
        validityMinutes: Int
    ): Either<Throwable, Unit> {
        val timestampInstant = Instant.fromEpochSeconds(timestamp).plus(
            value = validityMinutes,
            unit = DateTimeUnit.MINUTE
        )
        return if (timestampInstant < Clock.System.now()) {
            Either.Left(Exception(TooOldError))
        } else {
            Either.Right(Unit)
        }
    }

    private suspend fun verifyRequest(
        timestamp: Long,
        signature: String,
        rawBody: String?,
        signingSecret: String
    ): Either<Throwable, Unit> {
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
            Either.Left(Throwable(GenericError))
        }
    }
}

private val Version = "v0"
private val GenericError = "Request signature could not be verified"
private val TooOldError = "Request too old"