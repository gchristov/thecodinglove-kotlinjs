package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.commonfirebasedata.FirebaseAdmin
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthResponse
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import com.gchristov.thecodinglove.slackdata.api.ApiSlackPostMessageResponse
import com.gchristov.thecodinglove.slackdata.api.ApiSlackReplyWithMessageResponse
import com.gchristov.thecodinglove.slackdata.db.DbSlackAuthToken
import com.gchristov.thecodinglove.slackdata.db.toAuthToken
import com.gchristov.thecodinglove.slackdata.domain.SlackAuthToken
import com.gchristov.thecodinglove.slackdata.domain.toAuthToken
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

interface SlackRepository {
    suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String,
    ): Either<Throwable, SlackAuthToken>

    suspend fun getAuthToken(tokenId: String): Either<Throwable, SlackAuthToken>

    suspend fun saveAuthToken(token: SlackAuthToken): Either<Throwable, Unit>

    suspend fun deleteAuthToken(tokenId: String): Either<Throwable, Unit>

    suspend fun postMessageToUrl(
        url: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>

    suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>
}

internal class RealSlackRepository(
    private val apiService: SlackApi,
    private val firebaseAdmin: FirebaseAdmin,
    private val jsonSerializer: JsonSerializer,
) : SlackRepository {
    override suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String
    ) = try {
        val slackResponse: ApiSlackAuthResponse = apiService.authUser(
            code = code,
            clientId = clientId,
            clientSecret = clientSecret
        ).body()
        if (slackResponse.ok) {
            Either.Right(slackResponse.toAuthToken())
        } else {
            throw Exception(slackResponse.error)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during user auth${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun getAuthToken(tokenId: String): Either<Throwable, SlackAuthToken> = firebaseAdmin
        .firestore()
        .collection(AuthTokenCollection)
        .document(tokenId)
        .get()
        .flatMap { document ->
            if (document.exists) {
                document.decodeDataFromJson(
                    jsonSerializer = jsonSerializer,
                    strategy = DbSlackAuthToken.serializer(),
                ).flatMap { dbAuthToken ->
                    dbAuthToken?.let {
                        Either.Right(it.toAuthToken())
                    } ?: Either.Left(Exception("Slack auth token not found"))
                }
            } else {
                Either.Left(Exception("Slack auth token not found"))
            }
        }

    override suspend fun saveAuthToken(token: SlackAuthToken): Either<Throwable, Unit> = firebaseAdmin
        .firestore()
        .collection(AuthTokenCollection)
        .document(token.id)
        .set(
            jsonSerializer = jsonSerializer,
            strategy = DbSlackAuthToken.serializer(),
            data = token.toAuthToken(),
            merge = true,
        )

    override suspend fun deleteAuthToken(tokenId: String): Either<Throwable, Unit> = firebaseAdmin
        .firestore()
        .collection(AuthTokenCollection)
        .document(tokenId)
        .delete()

    override suspend fun postMessageToUrl(
        url: String,
        message: ApiSlackMessage
    ) = try {
        val slackResponse = apiService.postMessageToUrl(
            url = url,
            message = message
        )
        // Sending requests to Slack response URLs currently has an issue where the content type
        // does not honor the Accept header, so we get text/plain instead of application/json
        if (slackResponse.contentType()?.match(ContentType.Application.Json) == true) {
            val jsonResponse: ApiSlackReplyWithMessageResponse = slackResponse.body()
            if (jsonResponse.ok) {
                Either.Right(Unit)
            } else {
                throw Exception(jsonResponse.error)
            }
        } else {
            val textResponse = slackResponse.bodyAsText()
            if (textResponse.lowercase() == "ok") {
                Either.Right(Unit)
            } else {
                throw Exception(textResponse)
            }
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during message reply${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ) = try {
        val slackResponse: ApiSlackPostMessageResponse = apiService.postMessage(
            authToken = authToken,
            message = message
        ).body()
        if (slackResponse.ok) {
            Either.Right(Unit)
        } else {
            throw Exception(slackResponse.error)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during message post${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

private const val AuthTokenCollection = "slack_auth_token"