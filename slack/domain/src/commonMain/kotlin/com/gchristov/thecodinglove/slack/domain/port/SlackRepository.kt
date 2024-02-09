package com.gchristov.thecodinglove.slack.domain.port

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.model.SlackAuthToken
import com.gchristov.thecodinglove.slack.domain.model.SlackMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage

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
        message: SlackMessage,
    ): Either<Throwable, Unit>

    suspend fun postMessage(
        authToken: String,
        message: SlackMessage,
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