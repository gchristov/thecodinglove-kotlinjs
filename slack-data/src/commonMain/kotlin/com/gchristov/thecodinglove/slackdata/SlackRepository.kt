package com.gchristov.thecodinglove.slackdata

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.slackdata.api.*
import com.gchristov.thecodinglove.slackdata.db.DbSlackAuthToken
import com.gchristov.thecodinglove.slackdata.db.DbSlackSelfDestructMessage
import com.gchristov.thecodinglove.slackdata.db.toAuthToken
import com.gchristov.thecodinglove.slackdata.db.toSelfDestructMessage
import com.gchristov.thecodinglove.slackdata.domain.SlackAuthToken
import com.gchristov.thecodinglove.slackdata.domain.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slackdata.domain.toAuthToken
import com.gchristov.thecodinglove.slackdata.domain.toSelfDestructMessage
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

    suspend fun getAuthTokens(): Either<Throwable, List<SlackAuthToken>>

    suspend fun saveAuthToken(token: SlackAuthToken): Either<Throwable, Unit>

    suspend fun deleteAuthToken(tokenId: String): Either<Throwable, Unit>

    suspend fun postMessageToUrl(
        url: String,
        message: ApiSlackMessage
    ): Either<Throwable, Unit>

    suspend fun postMessage(
        authToken: String,
        message: ApiSlackMessage
    ): Either<Throwable, String>

    suspend fun deleteMessage(
        authToken: String,
        channelId: String,
        messageTs: String,
    ): Either<Throwable, Unit>

    suspend fun saveSelfDestructMessage(message: SlackSelfDestructMessage): Either<Throwable, Unit>

    suspend fun deleteSelfDestructMessage(messageId: String): Either<Throwable, Unit>

    suspend fun getSelfDestructMessages(): Either<Throwable, List<SlackSelfDestructMessage>>
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
                    } ?: Either.Left(Exception("Auth token not found"))
                }
            } else {
                Either.Left(Exception("Auth token not found"))
            }
        }

    override suspend fun getAuthTokens(): Either<Throwable, List<SlackAuthToken>> = firebaseAdmin
        .firestore()
        .collection(AuthTokenCollection)
        .get()
        .flatMap {
            it.docs
                .map { document ->
                    document.decodeDataFromJson(
                        jsonSerializer = jsonSerializer,
                        strategy = DbSlackAuthToken.serializer(),
                    ).flatMap { dbAuthToken ->
                        dbAuthToken?.let {
                            Either.Right(it.toAuthToken())
                        } ?: Either.Left(Throwable("Could not decode auth token"))
                    }
                }
                .let { l -> either { l.bindAll() } }
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
            Either.Right(slackResponse.messageTs)
        } else {
            throw Exception(slackResponse.error)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during message post${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun deleteMessage(
        authToken: String,
        channelId: String,
        messageTs: String,
    ): Either<Throwable, Unit> = try {
        val slackResponse: ApiSlackDeleteMessageResponse = apiService.deleteMessage(
            authToken = authToken,
            channelId = channelId,
            messageTs = messageTs,
        ).body()
        if (slackResponse.ok) {
            Either.Right(Unit)
        } else {
            throw Exception(slackResponse.error)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during message delete${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun saveSelfDestructMessage(message: SlackSelfDestructMessage): Either<Throwable, Unit> =
        firebaseAdmin
            .firestore()
            .collection(SelfDestructCollection)
            .document(message.id)
            .set(
                jsonSerializer = jsonSerializer,
                strategy = DbSlackSelfDestructMessage.serializer(),
                data = message.toSelfDestructMessage(),
                merge = true,
            )

    override suspend fun deleteSelfDestructMessage(messageId: String): Either<Throwable, Unit> = firebaseAdmin
        .firestore()
        .collection(SelfDestructCollection)
        .document(messageId)
        .delete()

    override suspend fun getSelfDestructMessages(): Either<Throwable, List<SlackSelfDestructMessage>> = firebaseAdmin
        .firestore()
        .collection(SelfDestructCollection)
        .get()
        .flatMap {
            it.docs
                .map { document ->
                    document.decodeDataFromJson(
                        jsonSerializer = jsonSerializer,
                        strategy = DbSlackSelfDestructMessage.serializer(),
                    ).flatMap { dbSelfDestructMessage ->
                        dbSelfDestructMessage?.let {
                            Either.Right(it.toSelfDestructMessage())
                        } ?: Either.Left(Throwable("Could not decode self-destruct message"))
                    }
                }
                .let { l -> either { l.bindAll() } }
        }
}

private const val AuthTokenCollection = "slack_auth_token"
private const val SelfDestructCollection = "slack_self_destruct"