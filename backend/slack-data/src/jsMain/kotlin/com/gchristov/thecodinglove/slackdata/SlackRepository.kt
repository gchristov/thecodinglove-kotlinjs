package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.slackdata.api.ApiSlackAuthResponse
import com.gchristov.thecodinglove.slackdata.api.ApiSlackMessage
import com.gchristov.thecodinglove.slackdata.api.ApiSlackPostMessageResponse
import com.gchristov.thecodinglove.slackdata.db.DbSlackAuthToken
import com.gchristov.thecodinglove.slackdata.db.toAuthToken
import com.gchristov.thecodinglove.slackdata.domain.SlackAuthToken
import com.gchristov.thecodinglove.slackdata.domain.toAuthToken
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.call.*
import io.ktor.client.statement.*

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
    private val log: Logger,
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
        log.e(error) { error.message ?: "Error during user auth" }
        Either.Left(error)
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
        log.e(error) { error.message ?: "Error during finding Slack auth token" }
        Either.Left(error)
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
        log.e(error) { error.message ?: "Error during saving Slack auth token" }
        Either.Left(error)
    }

    override suspend fun deleteAuthToken(tokenId: String): Either<Throwable, Unit> = try {
        firebaseFirestore
            .collection(AuthTokenCollection)
            .document(tokenId)
            .delete()
        Either.Right(Unit)
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during deleting Slack auth token" }
        Either.Left(error)
    }

    override suspend fun replyWithMessage(
        responseUrl: String,
        message: ApiSlackMessage
    ) = try {
        val slackResponse = apiService.replyWithMessage(
            responseUrl = responseUrl,
            message = message
        ).bodyAsText()
        if (slackResponse.lowercase() == "ok") {
            Either.Right(Unit)
        } else {
            throw Exception(slackResponse)
        }
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during message reply" }
        Either.Left(error)
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
        log.e(error) { error.message ?: "Error during message post" }
        Either.Left(error)
    }
}

private const val AuthTokenCollection = "slack_auth_token"