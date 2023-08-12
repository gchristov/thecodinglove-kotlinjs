package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthResponse
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import com.gchristov.thecodinglove.slackdata.api.ApiSlackPostMessageResponse
import com.gchristov.thecodinglove.slackdata.api.ApiSlackReplyWithMessageResponse
import com.gchristov.thecodinglove.slackdata.db.DbSlackAuthToken
import com.gchristov.thecodinglove.slackdata.db.toAuthToken
import com.gchristov.thecodinglove.slackdata.domain.SlackAuthToken
import com.gchristov.thecodinglove.slackdata.domain.toAuthToken
import dev.gitlive.firebase.firestore.FirebaseFirestore
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

    suspend fun replyWithMessage(
        responseUrl: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>

    suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>
}

internal class RealSlackRepository(
    private val apiService: SlackApi,
    private val firebaseFirestore: FirebaseFirestore,
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

    override suspend fun getAuthToken(tokenId: String): Either<Throwable, SlackAuthToken> = try {
        val document = firebaseFirestore
            .collection(AuthTokenCollection)
            .document(tokenId)
            .get()
        if (document.exists) {
            val dbAuthToken: DbSlackAuthToken = document.data()
            Either.Right(dbAuthToken.toAuthToken())
        } else {
            Either.Left(Exception("Slack auth token not found"))
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during finding Slack auth token${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun saveAuthToken(token: SlackAuthToken): Either<Throwable, Unit> = try {
        val document = firebaseFirestore
            .collection(AuthTokenCollection)
            .document(token.id)
        Either.Right(
            document.set(
                data = token.toAuthToken(),
                encodeDefaults = true,
                merge = true
            )
        )
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during saving Slack auth token${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun deleteAuthToken(tokenId: String): Either<Throwable, Unit> = try {
        firebaseFirestore
            .collection(AuthTokenCollection)
            .document(tokenId)
            .delete()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during deleting Slack auth token${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun replyWithMessage(
        responseUrl: String,
        message: ApiSlackMessage
    ) = try {
        val slackResponse = apiService.replyWithMessage(
            responseUrl = responseUrl,
            message = message
        )
        // Sending requests to Slack response URLs currently has an issue where the content type
        // does not honor the Accept header and we get text/plain, instead of application/json
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