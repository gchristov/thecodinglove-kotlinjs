package com.gchristov.thecodinglove.slack.adapter

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.slack.adapter.db.DbSlackAuthToken
import com.gchristov.thecodinglove.slack.adapter.db.DbSlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.adapter.db.toAuthToken
import com.gchristov.thecodinglove.slack.adapter.db.toSelfDestructMessage
import com.gchristov.thecodinglove.slack.adapter.http.SlackApi
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toAuthToken
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toSlackMessage
import com.gchristov.thecodinglove.slack.adapter.http.model.*
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthToken
import com.gchristov.thecodinglove.slack.domain.model.SlackMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*

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
            data = DbSlackAuthToken.of(token),
            merge = true,
        )

    override suspend fun deleteAuthToken(tokenId: String): Either<Throwable, Unit> = firebaseAdmin
        .firestore()
        .collection(AuthTokenCollection)
        .document(tokenId)
        .delete()

    override suspend fun postMessageToUrl(
        url: String,
        message: SlackMessage,
    ) = try {
        val slackResponse = apiService.postMessageToUrl(
            url = url,
            message = message.toSlackMessage(),
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
        message: SlackMessage,
    ) = try {
        val slackResponse: ApiSlackPostMessageResponse = apiService.postMessage(
            authToken = authToken,
            message = message.toSlackMessage(),
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
            deleteMessage = ApiSlackDeleteMessage(
                channelId = channelId,
                messageTs = messageTs,
            ),
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
                data = DbSlackSelfDestructMessage.of(message),
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