package com.gchristov.thecodinglove.slack.adapter

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.slack.SlackSender
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.slack.model.SlackMessage
import com.gchristov.thecodinglove.slack.adapter.db.DbSlackAuthToken
import com.gchristov.thecodinglove.slack.adapter.db.DbSlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.adapter.db.mapper.toAuthToken
import com.gchristov.thecodinglove.slack.adapter.db.mapper.toSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository

internal class RealSlackRepository(
    private val slackSender: SlackSender,
    private val firebaseAdmin: FirebaseAdmin,
    private val jsonSerializer: JsonSerializer,
) : SlackRepository {
    override suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String,
    ) = slackSender.authUser(
        code = code,
        clientId = clientId,
        clientSecret = clientSecret,
    )

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
        message: SlackMessage,
    ) = slackSender.postMessageToUrl(
        url = url,
        message = message,
    )

    override suspend fun postMessage(
        authToken: String,
        message: SlackMessage,
    ) = slackSender.postMessage(
        authToken = authToken,
        message = message,
    )

    override suspend fun deleteMessage(
        authToken: String,
        channelId: String,
        messageTs: String,
    ) = slackSender.deleteMessage(
        authToken = authToken,
        channelId = channelId,
        messageTs = messageTs,
    )

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
