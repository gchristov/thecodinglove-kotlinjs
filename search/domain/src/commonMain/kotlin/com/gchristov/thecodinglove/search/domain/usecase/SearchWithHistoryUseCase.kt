package com.gchristov.thecodinglove.search.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import com.gchristov.thecodinglove.search.domain.RandomResult
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.getExcludedPages
import com.gchristov.thecodinglove.search.domain.model.getExcludedPostIndexes
import com.gchristov.thecodinglove.search.domain.nextRandomPage
import com.gchristov.thecodinglove.search.domain.nextRandomPostIndex
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlin.random.Random

interface SearchWithHistoryUseCase {
    suspend operator fun invoke(dto: Dto): Either<Throwable, Result>

    sealed class Result {
        object Empty : Result()
        object Exhausted : Result()
        data class Data(
            val query: String,
            val totalPosts: Int,
            val post: SearchPost,
            val postPage: Int,
            val postIndexOnPage: Int,
            val postPageSize: Int
        ) : Result()
    }

    data class Dto(
        val query: String,
        val totalPosts: Int? = null,
        val searchHistory: Map<Int, List<Int>>,
    )
}

internal class RealSearchWithHistoryUseCase(
    private val dispatcher: CoroutineDispatcher,
    private val searchRepository: SearchRepository,
    private val searchConfig: SearchConfig,
) : SearchWithHistoryUseCase {
    override suspend operator fun invoke(
        dto: SearchWithHistoryUseCase.Dto
    ): Either<Throwable, SearchWithHistoryUseCase.Result> = withContext(dispatcher) {
        either {
            // Get total number of results for this query
            val totalResults = dto.totalPosts ?: searchRepository.getTotalPosts(dto.query).bind()
            if (totalResults <= 0) {
                return@either SearchWithHistoryUseCase.Result.Empty
            }

            // Pick a random page
            val postPageRes = Random.nextRandomPage(
                totalResults = totalResults,
                resultsPerPage = searchConfig.postsPerPage,
                exclusions = dto.searchHistory.getExcludedPages()
            )
            var postPage: Int
            when (postPageRes) {
                is RandomResult.Empty -> return@either SearchWithHistoryUseCase.Result.Empty
                is RandomResult.Exhausted -> return@either SearchWithHistoryUseCase.Result.Exhausted
                is RandomResult.Data -> postPage = postPageRes.result
            }

            // Get all posts for that page
            val searchResults = searchRepository.search(
                page = postPage,
                query = dto.query
            ).bind()
            if (searchResults.isEmpty()) {
                return@either SearchWithHistoryUseCase.Result.Empty
            }

            // Pick a random post on the page
            val postIndexOnPageRes = Random.nextRandomPostIndex(
                posts = searchResults,
                exclusions = dto.searchHistory.getExcludedPostIndexes(postPage)
            )
            var postIndexOnPage: Int
            when (postIndexOnPageRes) {
                is RandomResult.Empty -> return@either SearchWithHistoryUseCase.Result.Empty
                is RandomResult.Exhausted -> return@either SearchWithHistoryUseCase.Result.Exhausted
                is RandomResult.Data -> postIndexOnPage = postIndexOnPageRes.result
            }

            SearchWithHistoryUseCase.Result.Data(
                query = dto.query,
                totalPosts = totalResults,
                post = searchResults[postIndexOnPage],
                postPage = postPage,
                postIndexOnPage = postIndexOnPage,
                postPageSize = searchResults.size
            )
        }
    }
}