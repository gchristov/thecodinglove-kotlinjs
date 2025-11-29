package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.common.kotlin.debug
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.domain.model.insert
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface SearchUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Result>

    sealed class Type {
        data class WithSessionId(val sessionId: String) : Type()
        data class NewSession(val query: String) : Type()
    }

    sealed class Result {
        object Empty : Result()
        data class Data(
            val searchSessionId: String,
            val query: String,
            val post: SearchPost,
            val totalPosts: Int
        ) : Result()
    }

    data class Dto(val type: Type)
}

internal class RealSearchUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchWithHistoryUseCase: SearchWithHistoryUseCase,
    private val log: Logger,
) : SearchUseCase {
    private val tag = this::class.simpleName

    override suspend operator fun invoke(
        dto: SearchUseCase.Dto
    ): Either<Throwable, SearchUseCase.Result> = withContext(dispatcher) {
        either {
            val searchSession = dto.type.getSearchSession(searchRepository).bind()

            // If a post is preloaded, use it right away
            val preloadedPost = searchSession.preloadedPost
            if (preloadedPost != null) {
                log.debug(tag, "Using preloaded post")
                searchSession.usePreloadedPost(
                    preloadedPost = preloadedPost,
                    searchRepository = searchRepository
                ).bind()

                return@either SearchUseCase.Result.Data(
                    searchSessionId = searchSession.id,
                    query = searchSession.query,
                    post = preloadedPost,
                    totalPosts = searchSession.totalPosts ?: 0
                )
            }

            // Run normal search
            log.debug(tag, "No preloaded post, running normal search")
            val searchRes = searchWithHistoryUseCase(
                SearchWithHistoryUseCase.Dto(
                    query = searchSession.query,
                    totalPosts = searchSession.totalPosts,
                    searchHistory = searchSession.searchHistory,
                )
            ).bind()

            when (searchRes) {
                is SearchWithHistoryUseCase.Result.Empty -> SearchUseCase.Result.Empty
                is SearchWithHistoryUseCase.Result.Exhausted -> {
                    searchSession.clearExhaustedHistory(searchRepository).bind()
                    invoke(dto).bind()
                }
                is SearchWithHistoryUseCase.Result.Data -> {
                    searchSession.insertCurrentPost(
                        searchResult = searchRes,
                        searchRepository = searchRepository
                    ).bind()

                    SearchUseCase.Result.Data(
                        searchSessionId = searchSession.id,
                        query = searchRes.query,
                        post = searchRes.post,
                        totalPosts = searchRes.totalPosts
                    )
                }
            }
        }
    }
}

private suspend fun SearchUseCase.Type.getSearchSession(
    searchRepository: SearchRepository
): Either<Throwable, SearchSession> = when (this) {
    is SearchUseCase.Type.NewSession -> Either.Right(
        SearchSession(
            id = uuid4().toString(),
            query = this.query,
            totalPosts = null,
            searchHistory = emptyMap(),
            currentPost = null,
            preloadedPost = null,
            state = SearchSession.State.Searching()
        )
    )

    is SearchUseCase.Type.WithSessionId -> searchRepository.getSearchSession(this.sessionId)
}

private suspend fun SearchSession.insertCurrentPost(
    searchResult: SearchWithHistoryUseCase.Result.Data,
    searchRepository: SearchRepository
) = searchRepository.saveSearchSession(
    copy(
        totalPosts = searchResult.totalPosts,
        searchHistory = searchHistory.toMutableMap().apply {
            insert(
                postPage = searchResult.postPage,
                postIndexOnPage = searchResult.postIndexOnPage,
                currentPageSize = searchResult.postPageSize
            )
        },
        currentPost = searchResult.post
    )
)

private suspend fun SearchSession.clearExhaustedHistory(
    searchRepository: SearchRepository
) = searchRepository.saveSearchSession(
    copy(
        searchHistory = emptyMap(),
        currentPost = null,
    )
)

private suspend fun SearchSession.usePreloadedPost(
    preloadedPost: SearchPost,
    searchRepository: SearchRepository
) = searchRepository.saveSearchSession(
    copy(
        currentPost = preloadedPost,
        // Clear the preloaded post so that if it fails to load next time, we trigger search
        preloadedPost = null
    )
)