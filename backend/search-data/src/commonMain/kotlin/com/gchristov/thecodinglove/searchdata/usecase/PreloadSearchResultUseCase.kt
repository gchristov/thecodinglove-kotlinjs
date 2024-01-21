package com.gchristov.thecodinglove.searchdata.usecase

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface PreloadSearchResultUseCase {
    suspend operator fun invoke(searchSessionId: String) : Either<SearchError, Unit>
}

internal class RealPreloadSearchResultUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
) : PreloadSearchResultUseCase {
    override suspend operator fun invoke(
        searchSessionId: String
    ): Either<SearchError, Unit> = withContext(dispatcher) {
        searchRepository
            .getSearchSession(searchSessionId)
            .mapLeft { SearchError.SessionNotFound(additionalInfo = it.message) }
            .flatMap { searchSession ->
                searchWithHistoryUseCase(
                    query = searchSession.query,
                    totalPosts = searchSession.totalPosts,
                    searchHistory = searchSession.searchHistory,
                ).fold(
                    ifLeft = { searchError ->
                        when (searchError) {
                            is SearchError.Exhausted -> {
                                // Only clear the preloaded post and let session search deal with
                                // updating the history
                                searchSession
                                    .clearPreloadedPost(searchRepository)
                                    .mapLeft { SearchError.SessionNotFound(additionalInfo = it.message) }
                                    .flatMap { Either.Left(searchError) }
                            }

                            else -> Either.Left(searchError)
                        }
                    },
                    ifRight = { searchResult ->
                        searchSession
                            .insertPreloadedPost(
                                searchResult = searchResult,
                                searchRepository = searchRepository
                            )
                            .mapLeft { SearchError.SessionNotFound(additionalInfo = it.message) }
                    }
                )
            }
    }
}

private suspend fun SearchSession.insertPreloadedPost(
    searchResult: SearchWithHistoryUseCase.Result,
    searchRepository: SearchRepository
): Either<Throwable, Unit> {
    val updatedSearchSession = copy(
        totalPosts = searchResult.totalPosts,
        searchHistory = searchHistory.toMutableMap().apply {
            insert(
                postPage = searchResult.postPage,
                postIndexOnPage = searchResult.postIndexOnPage,
                currentPageSize = searchResult.postPageSize
            )
        },
        preloadedPost = searchResult.post
    )
    return searchRepository.saveSearchSession(updatedSearchSession)
}

private suspend fun SearchSession.clearPreloadedPost(searchRepository: SearchRepository) =
    searchRepository.saveSearchSession(copy(preloadedPost = null))