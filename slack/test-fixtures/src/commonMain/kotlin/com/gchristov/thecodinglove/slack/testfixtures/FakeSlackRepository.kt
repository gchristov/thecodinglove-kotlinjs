package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.slack.model.SlackAuthToken
import com.gchristov.thecodinglove.common.slack.model.SlackMessage
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeSlackRepository(
    private val authUserResult: Either<Throwable, SlackAuthToken> = Either.Right(SlackAuthTokenCreator.token()),
    private val getAuthTokenResult: Either<Throwable, SlackAuthToken> = Either.Left(Throwable("Auth token not found")),
    private val saveAuthTokenResult: Either<Throwable, Unit> = Either.Right(Unit),
    private val postMessageToUrlResult: Either<Throwable, Unit> = Either.Right(Unit),
    private val postMessageResult: Either<Throwable, String> = Either.Right(TestMessageTs),
    private val deleteMessageResult: Either<Throwable, Unit> = Either.Right(Unit),
    private val saveSelfDestructMessageResult: Either<Throwable, Unit> = Either.Right(Unit),
    private val deleteSelfDestructMessageResult: Either<Throwable, Unit> = Either.Right(Unit),
    private val getSelfDestructMessagesResult: Either<Throwable, List<SlackSelfDestructMessage>> = Either.Right(emptyList()),
) : SlackRepository {
    private var authUserInvocations = 0
    private var getAuthTokenInvocations = 0
    private var lastSavedAuthToken: SlackAuthToken? = null
    private var postMessageToUrlInvocations = 0
    private var postMessageInvocations = 0
    private var deleteMessageInvocations = 0
    private var lastSavedSelfDestructMessage: SlackSelfDestructMessage? = null
    private var deleteSelfDestructMessageInvocations = 0

    override suspend fun authUser(
        code: String,
        clientId: String,
        clientSecret: String,
    ) = authUserResult.also { authUserInvocations++ }

    override suspend fun getAuthToken(tokenId: String) =
        getAuthTokenResult.also { getAuthTokenInvocations++ }

    override suspend fun getAuthTokens(): Either<Throwable, List<SlackAuthToken>> =
        Either.Right(emptyList())

    override suspend fun saveAuthToken(token: SlackAuthToken) =
        saveAuthTokenResult.also { lastSavedAuthToken = token }

    override suspend fun deleteAuthToken(tokenId: String): Either<Throwable, Unit> =
        Either.Right(Unit)

    override suspend fun postMessageToUrl(url: String, message: SlackMessage) =
        postMessageToUrlResult.also { postMessageToUrlInvocations++ }

    override suspend fun postMessage(authToken: String, message: SlackMessage) =
        postMessageResult.also { postMessageInvocations++ }

    override suspend fun deleteMessage(
        authToken: String,
        channelId: String,
        messageTs: String,
    ) = deleteMessageResult.also { deleteMessageInvocations++ }

    override suspend fun saveSelfDestructMessage(message: SlackSelfDestructMessage) =
        saveSelfDestructMessageResult.also { lastSavedSelfDestructMessage = message }

    override suspend fun deleteSelfDestructMessage(messageId: String) =
        deleteSelfDestructMessageResult.also { deleteSelfDestructMessageInvocations++ }

    override suspend fun getSelfDestructMessages() = getSelfDestructMessagesResult

    fun assertAuthUserCalled() = assertTrue(authUserInvocations > 0)
    fun assertAuthTokenSaved(token: SlackAuthToken) = assertEquals(expected = token, actual = lastSavedAuthToken)
    fun assertAuthTokenNotSaved() = assertNull(lastSavedAuthToken)
    fun assertPostMessageToUrlCalledTimes(times: Int) = assertEquals(expected = times, actual = postMessageToUrlInvocations)
    fun assertPostMessageCalled() = assertTrue(postMessageInvocations > 0)
    fun assertPostMessageNotCalled() = assertEquals(expected = 0, actual = postMessageInvocations)
    fun assertDeleteMessageCalled() = assertTrue(deleteMessageInvocations > 0)
    fun assertDeleteMessageNotCalled() = assertFalse(deleteMessageInvocations > 0)
    fun assertSelfDestructMessageSaved() = assertNotNull(lastSavedSelfDestructMessage)
    fun assertSelfDestructMessageNotSaved() = assertNull(lastSavedSelfDestructMessage)
    fun assertDeleteSelfDestructMessageCalledTimes(times: Int) = assertEquals(expected = times, actual = deleteSelfDestructMessageInvocations)
}

const val TestMessageTs = "message_ts"
