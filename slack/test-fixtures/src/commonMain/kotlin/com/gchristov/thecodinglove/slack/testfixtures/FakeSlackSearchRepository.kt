package com.gchristov.thecodinglove.slack.testfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import kotlin.test.assertEquals

class FakeSlackSearchRepository(
    private val searchResult: Either<Throwable, SlackSearchRepository.SearchResultDto> = Either.Right(SlackSearchResultCreator.success()),
    private val shuffleResult: Either<Throwable, SlackSearchRepository.SearchResultDto> = Either.Right(SlackSearchResultCreator.success()),
    private val getSearchSessionPostResult: Either<Throwable, SlackSearchRepository.SearchSessionPostDto> = Either.Right(SlackSearchSessionPostCreator.post()),
    private val updateSearchSessionStateResult: Either<Throwable, Unit> = Either.Right(Unit),
    private val deleteSearchSessionResult: Either<Throwable, Unit> = Either.Right(Unit),
) : SlackSearchRepository {
    private var searchInvocations = 0
    private var shuffleInvocations = 0
    private var getSearchSessionPostInvocations = 0
    private var updateStateInvocations = 0
    private var deleteSessionInvocations = 0

    override suspend fun search(query: String) = searchResult.also { searchInvocations++ }

    override suspend fun shuffle(searchSessionId: String) = shuffleResult.also { shuffleInvocations++ }

    override suspend fun getSearchSessionPost(searchSessionId: String) =
        getSearchSessionPostResult.also { getSearchSessionPostInvocations++ }

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: SlackSearchRepository.SearchSessionStateDto,
    ) = updateSearchSessionStateResult.also { updateStateInvocations++ }

    override suspend fun deleteSearchSession(searchSessionId: String) =
        deleteSearchSessionResult.also { deleteSessionInvocations++ }

    fun assertSearchInvokedOnce() = assertEquals(expected = 1, actual = searchInvocations)
    fun assertShuffleInvokedOnce() = assertEquals(expected = 1, actual = shuffleInvocations)
    fun assertGetSessionPostInvokedOnce() = assertEquals(expected = 1, actual = getSearchSessionPostInvocations)
    fun assertGetSessionPostNotInvoked() = assertEquals(expected = 0, actual = getSearchSessionPostInvocations)
    fun assertUpdateStateCalledOnce() = assertEquals(expected = 1, actual = updateStateInvocations)
    fun assertUpdateStateNotCalled() = assertEquals(expected = 0, actual = updateStateInvocations)
    fun assertDeleteSessionInvokedOnce() = assertEquals(expected = 1, actual = deleteSessionInvocations)
}
