package com.gchristov.thecodinglove.slack.usecase

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.ApiRequest
import com.gchristov.thecodinglove.commonservice.bodyAsString
import com.gchristov.thecodinglove.commonservice.get
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import diglol.crypto.Hmac
import diglol.encoding.encodeHexToString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class RealVerifySlackRequestUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val slackConfig: SlackConfig,
) : VerifySlackRequestUseCase {
    override suspend fun invoke(request: ApiRequest): Either<Throwable, Unit> =
        withContext(dispatcher) {
            try {
                val timestamp: String = request.headers["x-slack-request-timestamp"]
                    ?: return@withContext Either.Left(Throwable(ErrorMessage))
                val signature: String = request.headers["x-slack-signature"]
                    ?: return@withContext Either.Left(Throwable(ErrorMessage))
                val rawBody = request.bodyAsString()
                verifySlackRequest(
                    timestamp = timestamp,
                    signature = signature,
                    rawBody = rawBody,
                    signingSecret = slackConfig.signingSecret
                )
            } catch (error: Throwable) {
                Either.Left(error)
            }
        }

    private suspend fun verifySlackRequest(
        timestamp: String,
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
            Either.Left(Throwable(ErrorMessage))
        }
    }
}

private val Version = "v0"
private val ErrorMessage = "Request signature could not be verified"