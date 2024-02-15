package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.domain.model.insert
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface PreloadSearchResultUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Unit>

    data class Dto(val searchSessionId: String)
}

internal class RealPreloadSearchResultUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
    private val log: Logger,
) : PreloadSearchResultUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(
        dto: PreloadSearchResultUseCase.Dto
    ): Either<Throwable, Unit> = withContext(dispatcher) {
        searchRepository
            .getSearchSession(dto.searchSessionId)
            .flatMap { searchSession ->
                searchWithHistoryUseCase(
                    SearchWithHistoryUseCase.Dto(
                        query = searchSession.query,
                        totalPosts = searchSession.totalPosts,
                        searchHistory = searchSession.searchHistory,
                    )
                ).fold(
                    ifLeft = { searchError ->
                        when (searchError) {
                            is SearchWithHistoryUseCase.Error.Exhausted -> {
                                // Only clear the preloaded post and let session search deal with
                                // updating the history
                                searchSession
                                    .clearPreloadedPost(searchRepository)
                                    .flatMap {
                                        log.error(tag, searchError) { "Search exhausted" }
                                        Either.Right(Unit)
                                    }
                            }

                            is SearchWithHistoryUseCase.Error.Empty -> Either.Left(searchError)
                        }
                    },
                    ifRight = { searchResult ->
                        searchSession.insertPreloadedPost(
                            searchResult = searchResult,
                            searchRepository = searchRepository
                        )
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